/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package crocserver.httphandler.google;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import crocserver.app.CrocApp;
import crocserver.app.CrocCookie;
import crocserver.app.CrocCookieMeta;
import vellumexp.json.JsonStrings;
import vellum.httpserver.Httpx;
import crocserver.storage.adminuser.AdminUser;
import java.io.IOException;
import java.util.Date;
import vellumexp.logr.Logr;
import vellumexp.logr.LogrFactory;
import vellum.util.Lists;

/**
 *
 * @author evan.summers
 */
public class GoogleLogoutHandler implements HttpHandler {

    Logr logger = LogrFactory.getLogger(getClass());
    CrocApp app;
    HttpExchange httpExchange;
    Httpx httpExchangeInfo;

    public GoogleLogoutHandler(CrocApp app) {
        super();
        this.app = app;
    }
    
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        this.httpExchange = httpExchange;
        httpExchangeInfo = new Httpx(httpExchange);
        logger.info("handle", getClass().getSimpleName(), httpExchangeInfo.getPath(), httpExchangeInfo.getParameterMap());
        try {
            handle();
        } catch (Exception e) {
            httpExchangeInfo.sendError(e);
        }
        httpExchange.close();
    }
        
    private void handle() throws Exception {
        CrocCookie cookie = new CrocCookie(httpExchangeInfo.getCookieMap());
        logger.info("cookie", cookie);
        AdminUser user = app.getStorage().getUserStorage().get(cookie.getEmail());
        user.setLogoutTime(new Date());
        app.getStorage().getUserStorage().updateLogout(user);
        httpExchangeInfo.clearCookie(Lists.toStringList(CrocCookieMeta.values()));
        httpExchangeInfo.sendResponse(cookie.toMap());
    }
}
