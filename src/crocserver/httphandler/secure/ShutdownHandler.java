/*
 * Source https://github.com/evanx by @evanxsummers
 */
package crocserver.httphandler.secure;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import crocserver.app.CrocApp;
import vellum.httpserver.Httpx;
import java.io.IOException;
import vellumexp.logr.Logr;
import vellumexp.logr.LogrFactory;
import crocserver.storage.common.CrocStorage;

/**
 *
 * @author evan.summers
 */
public class ShutdownHandler implements HttpHandler {

    Logr logger = LogrFactory.getLogger(getClass());
    CrocApp app;
    CrocStorage storage;
    HttpExchange httpExchange;
    Httpx httpExchangeInfo;

    public ShutdownHandler(CrocApp app) {
        super();
        this.app = app;
        this.storage = app.getStorage();
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        this.httpExchange = httpExchange;
        httpExchangeInfo = new Httpx(httpExchange);
        String remoteHostHame = httpExchange.getRemoteAddress().getHostName();
        logger.info("handle", getClass().getSimpleName(), httpExchangeInfo.getPath(), remoteHostHame);
        if (!remoteHostHame.equals("127.0.0.1")) {
            httpExchangeInfo.sendError(remoteHostHame);
        } else {
            try {
                app.stop();
                httpExchangeInfo.sendResponse("text/plain", true);
                httpExchangeInfo.getPrintStream().printf("OK %s\n", httpExchangeInfo.getPath());
            } catch (Exception e) {
                httpExchangeInfo.sendError(e);
            }
        }
        httpExchange.close();
    }
}
