/*
 * Source https://github.com/evanx by @evanxsummers
 */
package crocserver.httphandler.access;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import crocserver.app.CrocApp;
import vellum.httpserver.Httpx;
import java.io.IOException;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import vellumexp.logr.Logr;
import vellumexp.logr.LogrFactory;
import crocserver.storage.common.CrocStorage;
import crocserver.storage.org.Org;
import crocserver.storage.service.Service;
import java.security.cert.X509Certificate;
import java.util.Date;
import vellum.security.DefaultKeyStores;
import vellum.security.Dnames;
import vellumcert.Pems;
import vellum.util.Streams;
import vellumcert.CertReq;
import vellumcert.CertReqs;

/**
 *
 * @author evan.summers
 */
public class SignCertHandler implements HttpHandler {

    Logr logger = LogrFactory.getLogger(getClass());
    CrocApp app;
    CrocStorage storage;
    HttpExchange httpExchange;
    Httpx httpExchangeInfo;
    PrintStream out;
    String certReqPem;
    String userName;
    String orgName;
    String hostName;
    String clientName;

    public SignCertHandler(CrocApp app) {
        super();
        this.app = app;
        this.storage = app.getStorage();
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        this.httpExchange = httpExchange;
        httpExchangeInfo = new Httpx(httpExchange);
        httpExchange.getResponseHeaders().set("Content-type", "text/plain");
        certReqPem = Streams.readString(httpExchange.getRequestBody());
        out = new PrintStream(httpExchange.getResponseBody());
        logger.info(getClass().getSimpleName(), httpExchangeInfo.getPathArgs().length);
        if (httpExchangeInfo.getPathArgs().length == 6) {
            userName = httpExchangeInfo.getPathString(2);
            orgName = httpExchangeInfo.getPathString(3);
            hostName = httpExchangeInfo.getPathString(4);
            clientName = httpExchangeInfo.getPathString(5);
            try {
                sign();
            } catch (Exception e) {
                httpExchangeInfo.sendError(e);
            }
        } else {
            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, 0);
            out.printf("ERROR %s\n", httpExchangeInfo.getPath());
        }
        httpExchange.close();
    }

    private void sign() throws Exception {
        Org org = storage.getOrgStorage().get(orgName);
        String dname = Dnames.format(clientName, hostName, orgName,
                org.getRegion(), org.getLocality(), org.getCountry());
        logger.info("sign", dname, certReqPem.length());
        String alias = app.getServerKeyAlias();
        CertReq certReq = CertReqs.create(certReqPem);
        X509Certificate signedCert = CertReqs.sign(certReq,
                DefaultKeyStores.getPrivateKey(alias), DefaultKeyStores.getCert(alias),
                new Date(), 999);
        String signedCertPem = Pems.buildCertPem(signedCert);
        Service clientCert = storage.getServiceStorage().find(org.getId(), hostName, clientName);
        if (clientCert == null) {
            clientCert = new Service(org.getId(), hostName, clientName, userName);
            clientCert.setX509Cert(signedCert);
            storage.getServiceStorage().insert(clientCert);
        } else {
            logger.info("updateCert", clientCert.getId());
            clientCert.setX509Cert(signedCert);
            storage.getServiceStorage().updateCert(clientCert);
        }
        httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
        out.println(signedCertPem);
    }
}
