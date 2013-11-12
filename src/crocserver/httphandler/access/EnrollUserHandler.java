/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 */
package crocserver.httphandler.access;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import crocserver.app.CrocApp;
import vellum.httpserver.Httpx;
import crocserver.storage.adminuser.AdminUser;
import java.io.IOException;
import java.io.PrintStream;
import vellum.logr.Logr;
import vellum.logr.LogrFactory;
import crocserver.storage.common.CrocStorage;
import java.security.cert.X509Certificate;
import java.util.Date;
import sun.security.pkcs.PKCS10;
import vellum.datatype.Patterns;
import vellum.security.Certificates;
import vellum.security.DefaultKeyStores;
import vellum.security.Pems;

/**
 *
 * @author evan.summers
 */
public class EnrollUserHandler implements HttpHandler {

    Logr logger = LogrFactory.getLogger(getClass());
    CrocApp app;
    CrocStorage storage;
    HttpExchange httpExchange;
    Httpx httpExchangeInfo;
    PrintStream out;
    String certReqPem;
    String userName;

    public EnrollUserHandler(CrocApp app) {
        super();
        this.app = app;
        this.storage = app.getStorage();
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        this.httpExchange = httpExchange;
        httpExchangeInfo = new Httpx(httpExchange);
        httpExchange.getResponseHeaders().set("Content-type", "text/plain");
        logger.info("handle", getClass().getName(), httpExchangeInfo.getPath(), 
                httpExchangeInfo.getRequestBody(), httpExchangeInfo.getParameterMap());
        certReqPem = httpExchangeInfo.getRequestBody();
        out = httpExchangeInfo.getPrintStream();
        if (httpExchangeInfo.getPathLength() < 2) {
            httpExchangeInfo.handleError(httpExchangeInfo.getPath());
        } else {
            userName = httpExchangeInfo.getPathString(1);
            try {
                handle();
            } catch (Exception e) {
                httpExchangeInfo.handleError(e);
            }
        }
        httpExchange.close();
    }

    AdminUser user;

    private void handle() throws Exception {
        String email = httpExchangeInfo.getParameterMap().get("email");
        if (email == null) {
            email = userName;
        }
        if (!Patterns.matchesEmail(email)) {
            throw new Exception("email " + email);
        }
        user = app.getStorage().getUserStorage().find(userName);
        if (user == null) {
            user = new AdminUser(userName);
            user.setDisplayName(httpExchangeInfo.getParameterMap().get("displayName"));
            user.setEmail(email);
            user.formatSubject();
            user.setLocality(httpExchangeInfo.getParameterMap().get("locality"));
            user.setRegion(httpExchangeInfo.getParameterMap().get("region"));
            user.setCountry(httpExchangeInfo.getParameterMap().get("country"));
            user.setLoginTime(new Date());
            user.setEnabled(true);
        }
        logger.info("sign", user.getSubject(), certReqPem.length());
        if (!certReqPem.isEmpty()) {
            String alias = app.getServerKeyAlias();
            PKCS10 certReq = Pems.createCertReq(certReqPem);
            X509Certificate signedCert = Certificates.signCert(
                    DefaultKeyStores.getPrivateKey(alias), DefaultKeyStores.getCert(alias),
                    certReq, new Date(), 999);
            String signedCertPem = Pems.buildCertPem(signedCert);
            logger.info("subject", Pems.getSubjectDname(signedCertPem));
            logger.info("issuer", Pems.getIssuerDname(signedCertPem));
            storage.getUserStorage().store(user);
            if (false) {
                app.sendGtalkMessage(user.getEmail(), signedCert.getSubjectDN().toString());
            }
            httpExchangeInfo.sendResponse("application/x-pem-file", signedCertPem.getBytes());
        } else {
            storage.getUserStorage().store(user);
            httpExchangeInfo.write(user.getStringMap());    
        }
    }
}
