/*
 * Apache Software License 2.0, (c) Copyright 2012 Evan Summers
 */
package mantra.app;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import mantra.legacy.AdminHandler;
import mantra.legacy.ShutdownHandler;
import vellumexp.logr.Logr;
import vellumexp.logr.LogrFactory;

/**
 *
 * @author evan.summers
 */
public class MantraHttpHandler implements HttpHandler {
    Logr logger = LogrFactory.getLogger(MantraHttpHandler.class);
    MantraApp app;
    
    public MantraHttpHandler(MantraApp app) {
        this.app = app;
    }
    
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        HttpHandler handler = getHandler(httpExchange);
        if (handler != null) {
            handler.handle(httpExchange);
        } else {
            httpExchange.close();
        }
    }
    
    public HttpHandler getHandler(HttpExchange httpExchange) throws IOException {
        String path = httpExchange.getRequestURI().getPath();
        if (path.equals("/favicon.ico")) {
            return null;
        }
        logger.info("path", path);
        if (httpExchange.getRemoteAddress().getHostName().equals("127.0.0.1"))  {
            if (path.equals("/shutdown")) {
                return new ShutdownHandler(app);
            } else if (path.startsWith("/local/")) {
                return null;
            }
        }
        if (path.equals("/admin")) {
            return new AdminHandler(app);
        }
        return null;
    }
}
