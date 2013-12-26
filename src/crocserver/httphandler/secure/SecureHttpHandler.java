/*
 * Source https://github.com/evanx by @evanxsummers
 */
package crocserver.httphandler.secure;

import crocserver.httphandler.access.GenKeyP12Handler;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import crocserver.app.CrocApp;
import crocserver.httphandler.access.AccessHttpHandler;
import crocserver.httphandler.access.SignCertHandler;
import crocserver.httphandler.access.StoragePageHandler;
import crocserver.httphandler.access.ViewCertHandler;
import crocserver.httphandler.access.ViewOrgHandler;
import crocserver.httphandler.access.ViewServiceRecordHandler;
import crocserver.httphandler.access.ViewUserHandler;
import crocserver.storage.common.CrocStorage;
import java.io.IOException;
import vellumexp.logr.Logr;
import vellumexp.logr.LogrFactory;

/**
 *
 * @author evan.summers
 */
public class SecureHttpHandler implements HttpHandler {
    Logr logger = LogrFactory.getLogger(SecureHttpHandler.class);
    CrocApp app;
    CrocStorage storage;
    AccessHttpHandler childHandler; 
    
    public SecureHttpHandler(CrocApp app) {
        this.app = app;
        this.storage = app.getStorage();
        childHandler = new AccessHttpHandler(app);
    }
    
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        HttpHandler handler = getHandler(httpExchange);
        if (handler != null) {
            handler.handle(httpExchange);
        } else {
            childHandler.handle(httpExchange);
        }
    }
    
    public HttpHandler getHandler(HttpExchange httpExchange) throws IOException {
        String path = httpExchange.getRequestURI().getPath();
        logger.info("path", path);
        if (path.startsWith("/enableService/")) {
        } else if (path.equals("/shutdown")) {
            return new ShutdownHandler(app);
        } else if (path.startsWith("/genP12/")) {
            return new GenKeyP12Handler(app);
        } else if (path.startsWith("/signCert/")) {
            return new SignCertHandler(app);
        } else if (path.startsWith("/viewUser/")) {
            return new ViewUserHandler(storage);
        } else if (path.startsWith("/viewCert/")) {
            return new ViewCertHandler(storage);
        } else if (path.startsWith("/viewServiceRecord/")) {
            return new ViewServiceRecordHandler(storage);
        } else if (path.startsWith("/viewOrg/")) {
            return new ViewOrgHandler(storage);
        } else if (path.startsWith("/storage")) {
            return new StoragePageHandler(storage);
        } else if (path.startsWith("/post/")) {
            return new PostHandler(app);
        }
        return null;
    }
}
