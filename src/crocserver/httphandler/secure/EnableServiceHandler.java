/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 */
package crocserver.httphandler.secure;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import vellum.httpserver.HttpExchangeInfo;
import java.io.IOException;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import vellum.logr.Logr;
import vellum.logr.LogrFactory;
import crocserver.storage.common.CrocStorage;
import crocserver.storage.org.Org;
import crocserver.storage.service.Service;
import java.util.Date;
import vellum.security.DefaultKeyStores;
import vellum.security.KeyStores;
import common.crypto.GeneratedRsaKeyPair;

/**
 *
 * @author evan.summers
 */
public class EnableServiceHandler implements HttpHandler {
    Logr logger = LogrFactory.getLogger(getClass());
    CrocStorage storage;
    HttpExchange httpExchange;
    HttpExchangeInfo httpExchangeInfo;
    PrintStream out;

    String dname;
    String userName;
    String orgName;
    String hostName;
    String serviceName;
    String cert;
 
    Org org;
    
    public EnableServiceHandler(CrocStorage storage) {
        super();
        this.storage = storage;
    }
    
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        this.httpExchange = httpExchange;
        httpExchangeInfo = new HttpExchangeInfo(httpExchange);
        httpExchange.getResponseHeaders().set("Content-type", "text/plain");
        out = new PrintStream(httpExchange.getResponseBody());
        if (httpExchangeInfo.getPathArgs().length < 6) {
            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, 0);
            out.printf("ERROR %s\n", httpExchangeInfo.getPath());
        } else {
            userName = httpExchangeInfo.getPathString(2);
            orgName = httpExchangeInfo.getPathString(3);
            hostName = httpExchangeInfo.getPathString(4);
            serviceName = httpExchangeInfo.getPathString(5);
            try {
                handle();
            } catch (Exception e) {
                handle(e);
            }
        }
        httpExchange.close();
    }
    
    private void handle(Exception e) throws IOException {
        httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, 0);
        e.printStackTrace(out);
        e.printStackTrace(System.err);
        out.printf("ERROR %s\n", e.getMessage());
    }
    
    private void handle() throws Exception {
        org = storage.getOrgStorage().get(orgName);
        Service clientCert = storage.getServiceStorage().get(org.getId(), hostName, serviceName);
        httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
        out.println(clientCert.getCert());
    }
    
}
