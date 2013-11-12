/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 */
package crocserver.httphandler.access;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.ssl.internal.pkcs12.PKCS12KeyStore;
import crocserver.app.CrocApp;
import crocserver.app.CrocExceptionType;
import vellum.httpserver.Httpx;
import crocserver.storage.adminuser.AdminUser;
import java.io.IOException;
import vellum.logr.Logr;
import vellum.logr.LogrFactory;
import crocserver.storage.common.CrocStorage;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import vellum.crypto.rsa.GenRsaPair;
import vellum.exception.EnumException;
import vellum.security.DefaultKeyStores;

/**
 *
 * @author evan.summers
 */
public class GenKeyP12Handler implements HttpHandler {
    Logr logger = LogrFactory.getLogger(getClass());
    CrocApp app;
    CrocStorage storage;
    HttpExchange httpExchange;
    Httpx httpExchangeInfo;
    
    public GenKeyP12Handler(CrocApp app) {
        super();
        this.app = app;
        this.storage = app.getStorage();
    }
    
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        this.httpExchange = httpExchange;
        httpExchangeInfo = new Httpx(httpExchange);
        logger.info("handle", getClass().getSimpleName(), httpExchangeInfo.getParameterMap(), httpExchangeInfo.getCookieMap());
        try {
            handle();
        } catch (Exception e) {
            httpExchangeInfo.handleError(e);
        }
        httpExchange.close();
    }
    
    private void handle() throws Exception {
        AdminUser user = app.getUser(httpExchangeInfo, true);
        char[] password = httpExchangeInfo.getParameterMap().getString("password").toCharArray();
        if (true) {
            password = "defaultpw".toCharArray();
        }
        if (password.length < 8) {
            throw new EnumException(CrocExceptionType.PASSWORD_TOO_SHORT);
        }
        GenRsaPair keyPair = new GenRsaPair();
        keyPair.generate(user.formatSubject(), new Date(), 999, TimeUnit.DAYS);
        String alias = app.getServerKeyAlias();
        X509Certificate serverCert = app.getServerCert();
        keyPair.sign(DefaultKeyStores.getPrivateKey(alias), serverCert);
        user.setCert(keyPair.getCertificate());
        storage.getUserStorage().updateCert(user);
        storage.getCertStorage().save(keyPair.getCertificate());
        PKCS12KeyStore p12 = new PKCS12KeyStore();
        X509Certificate[] chain = new X509Certificate[] {keyPair.getCertificate(), serverCert};
        p12.engineSetKeyEntry(user.getUserName(), keyPair.getPrivateKey(), password, chain);
        httpExchangeInfo.sendResponseFile("application/x-pkcs12", "croc-client.p12");
        p12.engineStore(httpExchangeInfo.getPrintStream(), password);
    }
}
