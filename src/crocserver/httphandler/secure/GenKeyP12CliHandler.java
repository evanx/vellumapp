/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 */
package crocserver.httphandler.secure;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.ssl.internal.pkcs12.PKCS12KeyStore;
import crocserver.app.CrocApp;
import vellum.httpserver.HttpExchangeInfo;
import java.io.IOException;
import java.io.PrintStream;
import vellum.logr.Logr;
import vellum.logr.LogrFactory;
import crocserver.storage.common.CrocStorage;
import crocserver.storage.org.Org;
import crocserver.storage.service.Service;
import java.security.cert.X509Certificate;
import java.util.Date;
import vellum.security.Certificates;
import vellum.security.DefaultKeyStores;
import vellum.crypto.rsa.SignGenRsaPair;
import vellum.security.Pems;

/**
 *
 * @author evan.summers
 */
public class GenKeyP12CliHandler implements HttpHandler {
    Logr logger = LogrFactory.getLogger(getClass());
    CrocApp app;
    CrocStorage storage;
    HttpExchange httpExchange;
    HttpExchangeInfo httpExchangeInfo;
    PrintStream out;

    String userName;
    String orgName;
    String hostName;
    String clientName;
 
    public GenKeyP12CliHandler(CrocApp app) {
        super();
        this.app = app;
        this.storage = app.getStorage();
    }
    
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        this.httpExchange = httpExchange;
        httpExchangeInfo = new HttpExchangeInfo(httpExchange);
        out = new PrintStream(httpExchange.getResponseBody());
        if (httpExchangeInfo.getPathArgs().length < 6) {
            httpExchangeInfo.sendResponse("text/plain", true);
            out.printf("ERROR %s\n", httpExchangeInfo.getPath());
        } else {
            userName = httpExchangeInfo.getPathString(2);
            orgName = httpExchangeInfo.getPathString(3);
            hostName = httpExchangeInfo.getPathString(4);
            clientName = httpExchangeInfo.getPathString(5);
            try {
                handle();
            } catch (Exception e) {
                handle(e);
            }
        }
        httpExchange.close();
    }
    
    Org org;
    Service clientCert;
    
    private void handle() throws Exception {
        org = storage.getOrgStorage().get(orgName);
        if (org == null) {
            org = new Org(orgName);
            storage.getOrgStorage().insert(org);
        }
        String dname = Certificates.formatDname(clientName, hostName, orgName, 
                org.getRegion(), org.getLocality(), org.getCountry());
        logger.info("generate", dname);
        SignGenRsaPair keyPair = new SignGenRsaPair();
        keyPair.generate(dname, new Date(), 999);
        String alias = app.getServerKeyAlias();
        X509Certificate serverCert = DefaultKeyStores.getCert(alias);
        keyPair.sign(DefaultKeyStores.getPrivateKey(alias), serverCert);
        clientCert = storage.getServiceStorage().findSubject(dname);
        if (clientCert == null) {
            clientCert = new Service(org.getId(), hostName, clientName, userName);
        } else {
            clientCert.setUpdatedBy(userName);    
        }
        clientCert.setX509Cert(keyPair.getCertificate());
        if (clientCert.isStored()) {
            storage.getServiceStorage().updateCert(clientCert);    
        } else {
            storage.getServiceStorage().insert(clientCert);
        }
        PKCS12KeyStore p12 = new PKCS12KeyStore();
        X509Certificate[] chain = new X509Certificate[] {keyPair.getCertificate(), serverCert};
        char[] password = httpExchangeInfo.getParameterMap().
                getString("password", clientName).toCharArray();
        p12.engineSetKeyEntry(clientName, keyPair.getPrivateKey(), password, chain);
        if (httpExchangeInfo.getQuery().toLowerCase().contains("pem")) {
            httpExchangeInfo.sendResponse("text/plain", true);
            out.println(Pems.buildPem(p12, clientName, password));
            logger.info("pem");
        } else {
            httpExchangeInfo.sendResponse("application/x-pkcs12", true);
            p12.engineStore(out, "1234".toCharArray());
            logger.info("pkcs12");
        }
    }
    
    private void handle(Exception e) throws IOException {
        logger.warn(e, "p12");
        httpExchangeInfo.sendResponse("text/plain", true);
        e.printStackTrace(out);
        e.printStackTrace(System.err);
        out.printf("ERROR %s\n", e.getMessage());
    }
}
