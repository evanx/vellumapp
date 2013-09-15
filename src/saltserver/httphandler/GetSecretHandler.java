/*
 * Apache Software License 2.0, (c) Copyright 2012 Evan Summers
 * 
 */
package saltserver.httphandler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import vellum.httpserver.HttpExchangeInfo;
import java.io.IOException;
import java.util.Arrays;
import saltserver.app.VaultApp;
import saltserver.app.VaultStorage;
import saltserver.storage.secret.Secret;
import vellum.crypto.util.Base64;
import vellum.logr.Logr;
import vellum.logr.LogrFactory;
import vellum.util.Streams;

/**
 *
 * @author evan.summers
 */
public class GetSecretHandler implements HttpHandler {
    Logr logger = LogrFactory.getLogger(getClass());
    VaultApp app;
    VaultStorage storage;
    HttpExchange httpExchange;
    HttpExchangeInfo httpExchangeInfo;
    
    public GetSecretHandler(VaultApp app) {
        super();
        this.app = app;
    }

    String group;
    String name;
    String iv;
    
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        this.httpExchange = httpExchange;
        httpExchangeInfo = new HttpExchangeInfo(httpExchange);
        logger.info("handle", getClass().getSimpleName());
        if (httpExchangeInfo.getPathArgs().length < 3) {
            httpExchangeInfo.handleError(SaltServerError.INVALID_ARGS);
        } else {
            group = httpExchangeInfo.getPathString(1);
            name = httpExchangeInfo.getPathString(2);
            iv = Streams.readString(httpExchange.getRequestBody());    
            try {
                handle();
            } catch (Exception e) {
                httpExchangeInfo.handleError(e);
            }
        }
        httpExchange.close();
    }
    
    private void handle() throws Exception {
        Secret secret = app.getStorage().getSecretStorage().get(group, name);
        byte[] secretBytes = app.getCipher().decrypt(Base64.decode(secret.getSecret()), Base64.decode(iv));
        httpExchangeInfo.sendResponse("application/octet-stream", true);
        httpExchange.getResponseBody().write(secretBytes);
        Arrays.fill(secretBytes, Byte.MIN_VALUE);
    }
}
