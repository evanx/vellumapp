
/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 */
package crocserver.httphandler.access;

import crocserver.exception.CrocExceptionType;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import crocserver.app.CrocApp;
import crocserver.exception.CrocError;
import crocserver.exception.CrocException;
import crocserver.storage.adminuser.AdminUser;
import crocserver.storage.adminuser.AdminUserRole;
import crocserver.storage.clientcert.Cert;
import vellum.httpserver.HttpExchangeInfo;
import java.io.IOException;
import java.io.PrintStream;
import vellum.logr.Logr;
import vellum.logr.LogrFactory;

/**
 *
 * @author evan.summers
 */
public class GetCertHandler implements HttpHandler {
    Logr logger = LogrFactory.getLogger(getClass());
    CrocApp app;
    HttpExchange httpExchange;
    HttpExchangeInfo httpExchangeInfo;
    PrintStream out;

    String userName;
    String certName;
    
    public GetCertHandler(CrocApp app) {
        super();
        this.app = app;
    }
    
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        this.httpExchange = httpExchange;
        httpExchangeInfo = new HttpExchangeInfo(httpExchange);
        logger.info("handle", httpExchangeInfo.getPath());
        if (httpExchangeInfo.getPathArgs().length != 3) {
            httpExchangeInfo.handleError(new CrocError(CrocExceptionType.INVALID_ARGS, httpExchangeInfo.getPath()));
        } else {
            userName = httpExchangeInfo.getPathString(1);
            certName = httpExchangeInfo.getPathString(2);
            try {
                handle();
            } catch (Exception e) {
                httpExchangeInfo.handleError(e);
            }
        }
        httpExchange.close();
    }
    
    private void handle() throws Exception {
        AdminUser user = app.getStorage().getUserStorage().get(userName);
        Cert cert = app.getStorage().getCertStorage().findName(certName);
        if (cert == null) {
            throw new CrocException(CrocExceptionType.NOT_FOUND, certName);
        }
        app.getStorage().getOrgRoleStorage().verifyRole(
                user.getUserName(), cert.getOrgId(), AdminUserRole.SUPER);
        httpExchangeInfo.sendResponse("application/x-pem-file", 
                cert.getCert().getBytes());
    }    
}
