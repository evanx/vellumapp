/*
 * Source https://github.com/evanx by @evanxsummers
 */
package crocserver.httphandler.insecure;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import crocserver.app.CrocApp;
import java.io.IOException;
import vellum.logr.Logr;
import vellum.logr.LogrFactory;

/**
 *
 * @author evan.summers
 */
public class InsecureHomePageHandler implements HttpHandler {

    Logr logger = LogrFactory.getLogger(InsecureHomePageHandler.class);
    CrocApp app;
    
    public InsecureHomePageHandler(CrocApp app) {
        super();
        this.app = app;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        app.getWebHandler().handle(httpExchange);
    }
}
