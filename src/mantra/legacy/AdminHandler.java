package mantra.legacy;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.File;
import java.io.PrintStream;
import java.security.KeyStore;
import java.util.Collections;
import mantra.app.MantraApp;
import mantra.app.MantraKeyStoreManager;
import vellum.httpserver.Httpx;
import vellum.logr.Logr;
import vellum.logr.LogrFactory;
import vellum.util.Strings;

/**
 *
 *
 */
public class AdminHandler implements HttpHandler {

    Logr logger = LogrFactory.getLogger(getClass());
    HttpExchange httpExchange;
    Httpx httpExchangeInfo;
    MantraPageHandler handler;
    MantraApp app;
    PrintStream out;
    MantraKeyStoreManager keyStoreManager = new MantraKeyStoreManager(null);

    public AdminHandler(MantraApp app) {
        super();
        this.app = app;
        keyStoreManager = new MantraKeyStoreManager(app.getKeyStoreLocation());
    }

    @Override
    public void handle(HttpExchange httpExchange) {
        this.httpExchange = httpExchange;
        httpExchangeInfo = new Httpx(httpExchange);
        out = httpExchangeInfo.getPrintStream();
        handler = new MantraPageHandler(httpExchange);
        logger.info("handle", getClass().getSimpleName(), httpExchange.getRequestMethod(), httpExchangeInfo.getPath());
        try {
            handle();
        } catch (Exception e) {
            handler.handleException(e);
        }
        httpExchange.close();
    }

    private void handle() throws Exception {
        handler.printPageHeader("Admin");
        String username = httpExchangeInfo.getParameterMap().getString("username");
        String password = httpExchangeInfo.getParameterMap().getString("password");
        String keyStorePassword = httpExchangeInfo.getParameterMap().getString("keyStorePassword");
        if (true) {
            if (Strings.isEmpty(password)) {
                password = "password";
            }
            if (Strings.isEmpty(keyStorePassword)) {
                keyStorePassword = "password";
            }
        }
        logger.info("handle", keyStoreManager.getKeyStoreLocation());
        out.printf("<h3>%s %s</h3>\n", getClass().getSimpleName(),
                keyStoreManager.getKeyStoreLocation());
        out.printf("<form action='/admin' method='POST'>\n");
        logger.info("username", username);
        if (username != null && password != null) {
            app.getPasswordManager().put(username, password.toCharArray());
        }
        out.printf("<input type='password' name='keyStorePassword' width='40' placeholder='Key store password'><br>\n");
        if (!new File(keyStoreManager.getKeyStoreLocation()).exists()) {
            if (!Strings.isEmpty(keyStorePassword) && 
                    httpExchangeInfo.getParameterMap().containsKey("createKeyStore")) {
                logger.info("create", new File(keyStoreManager.getKeyStoreLocation()));
            } else {
                out.printf("<label for='createKeyStore'>Create key store</label>\n");
                out.printf("<input type='checkbox' name='createKeyStore'><br>\n");
            }
        } else if (!Strings.isEmpty(keyStorePassword)) {
            keyStoreManager.loadKeyStore(keyStorePassword.toCharArray());
            KeyStore keyStore = keyStoreManager.getKeyStore();
            out.printf("size %d<br>\n", keyStore.size());
            for (String alias : Collections.list(keyStore.aliases())) {
                out.printf("%s\n", alias);
                out.printf("<label for='deleteAlias-%s'>Delete alias</label>\n", alias);
                out.printf("<input type='checkbox' name='deleteAlias-%s'><br>\n", alias);
            }
            out.printf("<input type='text' name='genKeyAlias' width='40' placeholder='Generate key'><br>\n");
            if (Strings.isEmpty(httpExchangeInfo.getParameterMap().getString("genKeyAlias", null))) {
                generateKey();
            }
        } else {    
        }
        out.printf("<input type='text' name='username' width='40' placeholder='Username'>\n");
        out.printf("<input type='password' name='password' width='40' placeholder='Passphrase'><br>\n");
        out.printf("<input type='submit' value='Submit'>\n");
        out.printf("</form>\n");
        out.printf("<h3>Passwords on hand</h3>\n");
        if (app.getPasswordManager().getPasswordMap().isEmpty()) {
            out.printf("None<br>\n");
        } else {
            for (String key : app.getPasswordManager().getPasswordMap().keySet()) {
                out.printf("<span>%s</span><br>\n", key);
            }
        }
    }

    private void generateKey() {
    }
}
