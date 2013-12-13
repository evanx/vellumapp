/*
 * Source https://github.com/evanx by @evanxsummers
 */
package saltserver.httphandler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import vellum.util.JsonStrings;
import vellum.httpserver.Httpx;
import java.io.IOException;
import java.util.Arrays;
import saltserver.app.VaultApp;
import saltserver.app.VaultStorage;
import saltserver.storage.secret.Secret;
import vellum.util.Base64;
import vellum.crypto.api.Encrypted;
import vellum.logr.Logr;
import vellum.logr.LogrFactory;
import vellum.parameter.StringMap;
import vellum.util.Streams;

/**
 *
 * @author evan.summers
 */
public class PutSecretHandler implements HttpHandler {

    Logr logger = LogrFactory.getLogger(getClass());
    VaultApp app;
    VaultStorage storage;
    HttpExchange httpExchange;
    Httpx httpExchangeInfo;

    public PutSecretHandler(VaultApp app) {
        super();
        this.app = app;
    }
    String group;
    String name;
    byte[] secretBytes;

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        this.httpExchange = httpExchange;
        httpExchangeInfo = new Httpx(httpExchange);
        if (httpExchangeInfo.getPathArgs().length < 3) {
            httpExchangeInfo.handleError(SaltServerError.INVALID_ARGS);
        } else {
            group = httpExchangeInfo.getPathString(1);
            name = httpExchangeInfo.getPathString(2);
            secretBytes = Streams.readBytes(httpExchange.getRequestBody());
            try {
                logger.info("handle", getClass().getSimpleName(), group, name);
                handle();
            } catch (Exception e) {
                httpExchangeInfo.handleError(e);
            } finally {
                Arrays.fill(secretBytes, Byte.MIN_VALUE);
            }
        }
        httpExchange.close();
    }

    private void handle() throws Exception {
        StringMap responseMap = new StringMap();
        Secret secret = app.getStorage().getSecretStorage().find(group, name);
        Encrypted encrypted = app.getCipher().encrypt(secretBytes);
        String encodedSecret = Base64.encode(encrypted.getEncryptedBytes());
        if (secret == null) {
            secret = new Secret();
            secret.setGroup(group);
            secret.setName(name);
            secret.setSecret(encodedSecret);
            app.getStorage().getSecretStorage().insert(secret);
            responseMap.put("action", "inserted");
        } else {
            secret.setSecret(encodedSecret);
            app.getStorage().getSecretStorage().update(secret);
            responseMap.put("action", "updated");
        }
        responseMap.put("id", secret.getId());
        responseMap.put("iv", Base64.encode(encrypted.getIv()));
        String json = JsonStrings.buildJson(responseMap);
        httpExchangeInfo.sendResponse("text/json", true);
        httpExchangeInfo.getPrintStream().println(json);
        logger.info(json);
    }
}
