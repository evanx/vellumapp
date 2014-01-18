/*
 * Source https://github.com/evanx by @evanxsummers
 */
package crocserver.httphandler.access;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import crocserver.app.CrocApp;
import vellum.httpserver.Httpx;
import crocserver.storage.adminuser.AdminUser;
import java.io.IOException;
import java.io.PrintStream;
import vellumexp.logr.Logr;
import vellumexp.logr.LogrFactory;
import crocserver.storage.common.CrocStorage;
import java.security.cert.X509Certificate;
import java.util.Date;
import vellum.data.Patterns;
import vellum.security.DefaultKeyStores;
import vellumcert.Pems;
import vellumcert.CertReq;
import vellumcert.CertReqs;

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
                httpExchangeInfo.readString(), httpExchangeInfo.getParameterMap());
        certReqPem = httpExchangeInfo.readString();
        out = httpExchangeInfo.getPrintStream();
        if (httpExchangeInfo.getPathLength() < 2) {
            httpExchangeInfo.sendError(httpExchangeInfo.getPath());
        } else {
            userName = httpExchangeInfo.getPathString(1);
            try {
                handle();
            } catch (Exception e) {
                httpExchangeInfo.sendError(e);
            }
        }
        httpExchange.close();
    }

    AdminUser user;

    private void handle() throws Exception {
        String email = httpExchangeInfo.getParameterMap().getString("email", null);
        if (email == null) {
            email = userName;
        }
        if (!Patterns.matchesEmail(email)) {
            throw new Exception("email " + email);
        }
        user = app.getStorage().getUserStorage().find(userName);
        if (user == null) {
            user = new AdminUser(userName);
            user.setDisplayName(httpExchangeInfo.getParameterMap().getString("displayName", userName));
            user.setEmail(email);
            user.formatSubject();
            user.setLocality(httpExchangeInfo.getParameterMap().getString("locality", null));
            user.setRegion(httpExchangeInfo.getParameterMap().getString("region", null));
            user.setCountry(httpExchangeInfo.getParameterMap().getString("country", null));
            user.setLoginTime(new Date());
            user.setEnabled(true);
        }
        logger.info("sign", user.getSubject(), certReqPem.length());
        if (!certReqPem.isEmpty()) {
            String alias = app.getServerKeyAlias();
            CertReq certReq = CertReqs.create(certReqPem);
            X509Certificate signedCert = CertReqs.sign(certReq, 
                    DefaultKeyStores.getPrivateKey(alias), DefaultKeyStores.getCert(alias),
                    new Date(), 999);
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
            httpExchangeInfo.sendResponse(user.getStringMap());
        }
    }
}
