/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 */
package saltserver.httphandler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import vellum.httpserver.HttpExchangeInfo;
import java.io.IOException;
import saltserver.app.VaultApp;
import saltserver.app.VaultStorage;
import vellum.logr.Logr;
import vellum.logr.LogrFactory;

/**
 *
 * @author evan.summers
 */
public class ShutdownHandler implements HttpHandler {

    Logr logger = LogrFactory.getLogger(getClass());
    VaultApp app;
    VaultStorage storage;
    HttpExchange httpExchange;
    HttpExchangeInfo httpExchangeInfo;

    public ShutdownHandler(VaultApp app) {
        super();
        this.app = app;
        this.storage = app.getStorage();
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
