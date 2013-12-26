/*
 * Apache Software License 2.0, (c) Copyright 2012 Evan Summers
 */
package saltserver.app;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import saltserver.httphandler.AdminHandler;
import saltserver.httphandler.GetSecretHandler;
import saltserver.httphandler.ManagerHandler;
import saltserver.httphandler.PutSecretHandler;
import saltserver.httphandler.ShutdownHandler;
import vellumexp.logr.Logr;
import vellumexp.logr.LogrFactory;

/**
 *
 * @author evan.summers
 */
public class VaultHttpHandler implements HttpHandler {
    Logr logger = LogrFactory.getLogger(VaultHttpHandler.class);
    VaultApp app;
    VaultStorage storage;
    
    public VaultHttpHandler(VaultApp app) {
        this.app = app;
        this.storage = app.getStorage();
    }
    
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        HttpHandler handler = getHandler(httpExchange);
        if (handler != null) {
            handler.handle(httpExchange);
        } else {
            
        }
    }
    
    public HttpHandler getHandler(HttpExchange httpExchange) throws IOException {
        String path = httpExchange.getRequestURI().getPath();
        logger.info("path", path);
        if (httpExchange.getRemoteAddress().getHostName().equals("127.0.0.1"))  {
            if (path.equals("/shutdown")) {
                return new ShutdownHandler(app);
            } else if (path.equals("/manager")) {
                return new ManagerHandler(app);
            } else if (path.startsWith("/local/")) {
                return null;
            }
        }
        if (path.startsWith("/getSecret/")) {
            return new GetSecretHandler(app);
        } else if (path.startsWith("/putSecret/")) {
            return new PutSecretHandler(app);
        } else if (path.equals("/admin")) {
            return new AdminHandler(app);
        }
        return null;
    }
}
