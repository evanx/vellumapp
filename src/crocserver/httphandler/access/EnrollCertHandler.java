/*
 * Source https://github.com/evanx by @evanxsummers
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
import crocserver.storage.org.Org;
import vellum.httpserver.Httpx;
import java.io.IOException;
import vellum.logr.Logr;
import vellum.logr.LogrFactory;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import vellum.crypto.rsa.GenRsaPair;
import vellum.data.Emails;
import vellum.security.DefaultKeyStores;
import vellum.security.Pems;

/**
 *
 * @author evan.summers
 */
public class EnrollCertHandler implements HttpHandler {
    Logr logger = LogrFactory.getLogger(getClass());
    CrocApp app;
    HttpExchange httpExchange;
    Httpx httpExchangeInfo;

    String userName;
    String orgName;
    String certName;
 
    public EnrollCertHandler(CrocApp app) {
        super();
        this.app = app;
    }
    
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        this.httpExchange = httpExchange;
        httpExchangeInfo = new Httpx(httpExchange);
        logger.info("handle", getClass().getName(), httpExchangeInfo.getPathArgs());
        if (httpExchangeInfo.getPathArgs().length != 4) {
            httpExchangeInfo.handleError(new CrocError(
                    CrocExceptionType.INVALID_ARGS, httpExchangeInfo.getPath()));
        } else {
            userName = httpExchangeInfo.getPathString(1);
            orgName = httpExchangeInfo.getPathString(2);
            certName = httpExchangeInfo.getPathString(3);
            try {
                handle();
            } catch (Exception e) {
                httpExchangeInfo.handleError(e);
            }
        }
        httpExchange.close();
    }
    
    private void handle() throws Exception {
        AdminUser user = app.getStorage().getUserStorage().find(userName);
        Org org = app.getStorage().getOrgStorage().get(orgName);
        app.getStorage().getOrgRoleStorage().verifyRole(user, org, AdminUserRole.SUPER);
        logger.info("handle", user.getUserName(), org.getOrgName());
        GenRsaPair keyPair = new GenRsaPair();
        if (!Emails.matchesEmail(certName)) {
            throw new CrocException(CrocExceptionType.CERT_NAME_NOT_EMAIL_FORMAT, certName);
        }
        String dname = org.formatDname(certName, userName);
        keyPair.generate(dname, new Date(), 999, TimeUnit.DAYS);
        String alias = app.getServerKeyAlias();
        keyPair.sign(DefaultKeyStores.getPrivateKey(alias), DefaultKeyStores.getCert(alias));
        Cert cert = app.getStorage().getCertStorage().findName(certName);
        if (cert == null) {
            cert = new Cert();
            cert.setOrgId(org.getId());
        }
        cert.setCert(keyPair.getCertificate());
        app.getStorage().getCertStorage().save(cert);
        httpExchangeInfo.sendResponse("application/x-pem-file",
                Pems.buildKeyPem(keyPair.getPrivateKey()).getBytes());
    }
}
