/*
 * Source https://github.com/evanx by @evanxsummers
 */
package crocserver.httphandler.access;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import crocserver.app.CrocApp;
import vellum.httpserver.Httpx;
import crocserver.storage.adminuser.AdminUser;
import crocserver.storage.adminuser.AdminUserRole;
import crocserver.storage.common.CrocStorageHandler;
import java.io.IOException;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import vellumexp.logr.Logr;
import vellumexp.logr.LogrFactory;
import crocserver.storage.org.Org;
import crocserver.storage.orgrole.OrgRole;
import java.util.List;
import vellum.data.Patterns;

/**
 *
 * @author evan.summers
 */
public class EnrollOrgHandler extends CrocStorageHandler implements HttpHandler {
    Logr logger = LogrFactory.getLogger(getClass());
    HttpExchange httpExchange;
    Httpx httpExchangeInfo;
    PrintStream out;

    public EnrollOrgHandler(CrocApp app) {
        super(app);
    }

    String userName;
    String orgName;
    
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        this.httpExchange = httpExchange;
        httpExchangeInfo = new Httpx(httpExchange);
        logger.info("handle", getClass().getSimpleName(), httpExchangeInfo.getParameterMap(), httpExchangeInfo.getCookieMap());
        if (httpExchangeInfo.getPathLength() < 2) {
            httpExchangeInfo.sendError(httpExchangeInfo.getPath());
        } else {
            userName = httpExchangeInfo.getPathString(1);
        }
        orgName = httpExchangeInfo.getPathString(2);
        try {
            handle();
        } catch (Exception e) {
            httpExchangeInfo.sendError(e);
        }
        httpExchange.close();
    }

    Org org;
    
    private void handle() throws Exception {
        AdminUser user = app.getUser(httpExchangeInfo, true);
        logger.info("user", user);
        String url = httpExchangeInfo.getParameterMap().getString("url", null);
        if (url == null) {
            url = orgName;
        }
        if (url != null) {
            if (!Patterns.matchesDomain(url)) {
                throw new Exception("url " + url);
            }
        }
        String orgName = url;
        org = storage.getOrgStorage().find(orgName);
        if (org == null) {
            org = new Org(orgName);
        }
        org.setDisplayName(httpExchangeInfo.getParameterMap().getString("displayName", url));
        org.setUrl(url);
        org.setRegion(httpExchangeInfo.getParameterMap().getString("region", null));
        org.setLocality(httpExchangeInfo.getParameterMap().getString("locality", null));
        org.setCountry(httpExchangeInfo.getParameterMap().getString("country", null));
        if (org.isStored()) {
            storage.getOrgStorage().update(org);
        } else {
            storage.getOrgStorage().insert(org);
        }
        List<OrgRole> orgRoleList = storage.getOrgRoleStorage().getOrgRoleList(user, org);
        if (orgRoleList.isEmpty()) {
            OrgRole orgRole = new OrgRole(user, org, AdminUserRole.SUPER);
            storage.getOrgRoleStorage().insert(orgRole);
        }
        httpExchangeInfo.sendResponse(org.getMap());
    }
}
