/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 */
package crocserver.httphandler.access;

import crocserver.httphandler.google.OAuthCallbackHandler;
import crocserver.httphandler.google.GoogleLoginHandler;
import crocserver.httphandler.google.GoogleLogoutHandler;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import crocserver.app.CrocApp;
import crocserver.httphandler.persona.PersonaLoginHandler;
import crocserver.httphandler.persona.PersonaLogoutHandler;
import crocserver.httphandler.secure.SecureHomeHandler;
import crocserver.httphandler.secure.ShutdownHandler;
import java.io.IOException;
import vellum.logr.Logr;
import vellum.logr.LogrFactory;
import crocserver.storage.common.CrocStorage;

/**
 *
 * @author evan.summers
 */
public class AccessHttpHandler implements HttpHandler {

    Logr logger = LogrFactory.getLogger(AccessHttpHandler.class);
    CrocApp app;
    CrocStorage storage;
    
    public AccessHttpHandler(CrocApp app) {
        this.app = app;
        storage = app.getStorage();
    }
    
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        HttpHandler handler = getHandler(httpExchange);
        if (handler == null) {
            handler = app.getWebHandler();
        }
        handler.handle(httpExchange);
    }

    public HttpHandler getHandler(HttpExchange httpExchange) throws IOException {
        String path = httpExchange.getRequestURI().getPath();
        logger.info("path", path);
        if (path.startsWith("/oauth")) {
            return new OAuthCallbackHandler(app);
        } else if (path.startsWith("/echo")) {
            return new EchoHandler(app);
        } else if (path.startsWith("/admin")) {
            return new SecureHomeHandler(app);
        } else if (path.equals("/editOrg")) {
            return new EditOrgHandler(app);
        } else if (path.startsWith("/enrollUser/")) {
            return new EnrollUserHandler(app);
        } else if (path.startsWith("/enrollOrg/")) {
            return new EnrollOrgHandler(app);
        } else if (path.startsWith("/enrollCert/")) {
            return new EnrollCertHandler(app);
        } else if (path.startsWith("/getCert/")) {
            return new GetCertHandler(app);
        } else if (path.equals("/login")) {
            return new GoogleLoginHandler(app);
        } else if (path.equals("/logout")) {
            return new GoogleLogoutHandler(app);
        } else if (path.equals("/loginPersona")) {
            return new PersonaLoginHandler(app);
        } else if (path.equals("/logoutPersona")) {
            return new PersonaLogoutHandler(app);
        } else if (path.equals("/genKey")) {
            return new GenKeyP12Handler(app);
        } else if (path.startsWith("/signCert/")) {
            return new SignCertHandler(app);
        } else if (path.startsWith("/viewUser/")) {
            return new ViewUserHandler(storage);
        } else if (path.startsWith("/viewCert/")) {
            return new ViewCertHandler(storage);
        } else if (path.startsWith("/viewService/")) {
            return new ViewServiceHandler(storage);
        } else if (path.startsWith("/viewServiceRecord/")) {
            return new ViewServiceRecordHandler(storage);
        } else if (path.startsWith("/viewOrg/")) {
            return new ViewOrgHandler(storage);
        } else if (path.startsWith("/storage")) {
            return new StoragePageHandler(storage);
        } else if (path.equals("/shutdown")) {
            return new ShutdownHandler(app);
        }
        return null;
    }    
}
