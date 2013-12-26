/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package crocserver.httphandler.google;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import crocserver.app.CrocApp;
import crocserver.app.CrocCookie;
import crocserver.app.CrocSecurity;
import crocserver.app.GoogleUserInfo;
import vellumexp.json.JsonStrings;
import vellum.httpserver.Httpx;
import crocserver.storage.adminuser.AdminUser;
import java.io.IOException;
import java.util.Date;
import vellumexp.logr.Logr;
import vellumexp.logr.LogrFactory;
import vellum.parameter.StringMap;
import vellum.util.Strings;

/**
 *
 * @author evan.summers
 */
public class GoogleLoginHandler implements HttpHandler {

    Logr logger = LogrFactory.getLogger(getClass());
    CrocApp app;
    HttpExchange httpExchange;
    Httpx httpExchangeInfo;

    public GoogleLoginHandler(CrocApp app) {
        super();
        this.app = app;
    }
    
    String userId;
    String accessToken;
    
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        this.httpExchange = httpExchange;
        httpExchangeInfo = new Httpx(httpExchange);
        logger.info("handle", getClass().getSimpleName(), httpExchangeInfo.getPath(), 
                httpExchangeInfo.getParameterMap());
        if (httpExchangeInfo.getPath().length() == 0) {
            httpExchange.close();
            return;
        }
        accessToken = httpExchangeInfo.getParameterMap().getString("accessToken", null);
        logger.info("input", userId, accessToken);
        try {
            if (accessToken != null) {
                handle();
            } else {
                httpExchangeInfo.sendError("require access_token");
            }
        } catch (Exception e) {
            httpExchangeInfo.sendError(e);
        }
        httpExchange.close();
    }
    
    GoogleUserInfo userInfo;
    
    private void handle() throws Exception {
        userInfo = app.getGoogleApi().getUserInfo(accessToken);
        logger.info("userInfo", userInfo);
        AdminUser user = app.getStorage().getUserStorage().findEmail(userInfo.getEmail());
        if (user == null) {
            user = new AdminUser(userInfo.getEmail());
            user.setDisplayName(userInfo.getDisplayName());
            user.setFirstName(userInfo.getGivenName());
            user.setLastName(userInfo.getFamilyName());
            user.setEmail(userInfo.getEmail());
            user.setEnabled(true);
            user.setSecret(CrocSecurity.createSecret());
        }
        user.setLoginTime(new Date());
        if (user.isStored()) {
            app.getStorage().getUserStorage().update(user);
        } else {
            app.getStorage().getUserStorage().insert(user);
        }
        String totpUrl = CrocSecurity.getTotpUrl(
                user.getFirstName().toLowerCase(), app.getServerName(), user.getSecret());
        String qrUrl = CrocSecurity.getQrCodeUrl(
                user.getFirstName().toLowerCase(), app.getServerName(), user.getSecret());
        logger.info("qrUrl", qrUrl, Strings.decodeUrl(qrUrl));
        CrocCookie cookie = new CrocCookie(
                user.getEmail(), user.getDisplayName(), user.getLoginTime().getTime(), accessToken);
        cookie.createAuthCode(user.getSecret().getBytes());
        httpExchangeInfo.setCookie(cookie.toMap(), CrocCookie.MAX_AGE_MILLIS);
        httpExchangeInfo.sendResponse("text/json", true);
        StringMap responseMap = new StringMap();
        responseMap.put("email", userInfo.getEmail());
        responseMap.put("name", userInfo.getDisplayName());
        responseMap.put("picture", userInfo.getPicture());
        responseMap.put("qr", qrUrl);
        responseMap.put("totpSecret", user.getSecret());
        responseMap.put("totpUrl", totpUrl);
        responseMap.put("authCode", cookie.getAuthCode());
        String json = JsonStrings.buildJson(responseMap);
        httpExchangeInfo.getPrintStream().println(json);
        logger.info(json);
    }
}
