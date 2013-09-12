/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 */
package crocserver.httphandler.google;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import crocserver.app.CrocApp;
import crocserver.app.CrocSecurity;
import crocserver.app.GoogleUserInfo;
import vellum.httpserver.HttpExchangeInfo;
import crocserver.storage.adminuser.AdminUserRole;
import crocserver.storage.adminuser.AdminUser;
import java.io.IOException;
import java.io.PrintStream;
import vellum.html.HtmlPrinter;
import vellum.logr.Logr;
import vellum.logr.LogrFactory;
import vellum.util.Strings;

/**
 *
 * @author evan.summers
 */
public class OAuthCallbackHandler implements HttpHandler {

    Logr logger = LogrFactory.getLogger(getClass());
    CrocApp app;
    HttpExchange httpExchange;
    HttpExchangeInfo httpExchangeInfo;
    PrintStream out;

    public OAuthCallbackHandler(CrocApp app) {
        super();
        this.app = app;
    }
    String state;
    String accessToken;
    String code;
    String error;
    Integer expiry;
    
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        this.httpExchange = httpExchange;
        httpExchangeInfo = new HttpExchangeInfo(httpExchange);
        logger.info("handle", getClass().getSimpleName(), httpExchangeInfo.getPath(), httpExchangeInfo.getParameterMap());
        if (httpExchangeInfo.getPath().length() == 0) {
            httpExchange.close();
            return;
        }
        state = httpExchangeInfo.getParameter("state");
        accessToken = httpExchangeInfo.getParameter("access_token");
        expiry = httpExchangeInfo.getInteger("expires_in");
        code = httpExchangeInfo.getParameter("code");
        error = httpExchangeInfo.getParameter("error");
        try {
            if (error != null) {
                httpExchangeInfo.handleError(error);
            } else if (code != null) {
                handle();
            } else {
                httpExchangeInfo.handleError("internal error");
            }
        } catch (Exception e) {
            httpExchangeInfo.handleError(e);
        }
        httpExchange.close();
    }
    
    GoogleUserInfo userInfo;    
    
    private void handle() throws Exception {
        userInfo = app.getGoogleApi().sendTokenRequest(code);
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
        if (user.isStored()) {
            app.getStorage().getUserStorage().update(user);
        } else {
            app.getStorage().getUserStorage().insert(user);
        }
        String qrUrl = CrocSecurity.getQrCodeUrl(user.getFirstName().toLowerCase(), app.getServerName(), user.getSecret());
        logger.info("qrUrl", qrUrl, Strings.decodeUrl(qrUrl));
        String signCertUrl = String.format("%s/sign/userCert/%s", app.getServerUrl(), user.getEmail());
        httpExchangeInfo.sendResponse("text/html", true);
        HtmlPrinter p = new HtmlPrinter(httpExchange.getResponseBody());
        p.div("menuBarDiv");
        p.aClosed("/", "Home");
        p.divClose();
        p.h(2, "Welcome, " + userInfo.getDisplayName());
        p.span("", String.format("The following provided email address will be used as your username: <tt>%s</tt>", userInfo.getEmail()));
        p.println("<p>");
        p.span("", String.format("Your secret for TOPT is: <tt>%s</tt>", user.getSecret()));
        p.println("<br>");
        p.span("", String.format("You can enter the above, or scan the following, into your Google Authenticator."));
        p.println("<p>");
        p.aimg(qrUrl, qrUrl);
        p.println("<p>");
        p.span("", String.format("Please paste your CSR for your private key:"));
        p.println("<p>");
        p.form();
        p.textarea("csr", 10, 80, null);
        p.formClose();
        p.divClose();
    }    
}
