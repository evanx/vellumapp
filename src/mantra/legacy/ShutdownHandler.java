/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 */
package mantra.legacy;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import vellum.httpserver.HttpExchangeInfo;
import java.io.IOException;
import mantra.app.MantraApp;
import vellum.logr.Logr;
import vellum.logr.LogrFactory;

/**
 *
 * @author evan.summers
 */
public class ShutdownHandler implements HttpHandler {

    Logr logger = LogrFactory.getLogger(getClass());
    MantraApp app;
    HttpExchange httpExchange;
    HttpExchangeInfo httpExchangeInfo;

    public ShutdownHandler(MantraApp app) {
        super();
        this.app = app;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        this.httpExchange = httpExchange;
        httpExchangeInfo = new HttpExchangeInfo(httpExchange);
        logger.info("handle", getClass().getSimpleName(), httpExchangeInfo.getPath());
        try {
            httpExchangeInfo.sendResponse("text/plain", true);
            httpExchangeInfo.getPrintStream().printf("OK %s\n", httpExchangeInfo.getPath());
            httpExchange.close();
            app.stop();
        } catch (Exception e) {
            httpExchangeInfo.handleError(e);
            httpExchange.close();
        }
    }
}
