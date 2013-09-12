/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 */
package crocserver.httphandler.access;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import crocserver.app.CrocApp;
import vellum.httpserver.HttpExchangeInfo;
import crocserver.storage.adminuser.AdminUser;
import java.io.IOException;
import java.io.PrintStream;
import vellum.logr.Logr;
import vellum.logr.LogrFactory;
import vellum.storage.StorageException;
import crocserver.storage.org.Org;
import vellum.storage.StorageExceptionType;

/**
 *
 * @author evan.summers
 */
public class EditOrgHandler implements HttpHandler {
    Logr logger = LogrFactory.getLogger(getClass());
    CrocApp app;
    HttpExchange httpExchange;
    HttpExchangeInfo httpExchangeInfo;
    PrintStream out;

    public EditOrgHandler(CrocApp app) {
        super();
        this.app = app;
    }
    
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        this.httpExchange = httpExchange;
        httpExchangeInfo = new HttpExchangeInfo(httpExchange);
        logger.info("handle", getClass().getName(), httpExchangeInfo.getParameterMap());
        httpExchange.getResponseHeaders().set("Content-type", "text/plain");
        try {
            handle();
        } catch (Exception e) {
            httpExchangeInfo.handleError(e);
        }
        httpExchange.close();
    }

    Org org;
    
    private void handle() throws Exception {
        AdminUser user = app.getUser(httpExchangeInfo, true);
        logger.info("user", user);
        Org bean = new Org(httpExchangeInfo.getParameterMap());
        org = app.getStorage().getOrgStorage().find(bean.getOrgName());
        if (org == null) {
            org = bean;
            org.validate();
            logger.info("insert", org);
            app.getStorage().getOrgStorage().insert(org);
        } else {
            org.update(bean);
            org.validate();
            logger.info("update", org);
            app.getStorage().getOrgStorage().update(org);    
        }
        httpExchangeInfo.write(org.getStringMap());
    }
    
}
