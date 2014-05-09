/*
 * Source https://github.com/evanx by @evanxsummers
 */
package encrypto.handler;

import com.sun.net.httpserver.HttpExchange;
import encrypto.app.EncryptoApp;
import java.io.IOException;
import java.net.HttpURLConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.jx.JMaps;

/**
 *
 * @author evan.summers
 */
public class ErrorHttpHandler {

    static Logger logger = LoggerFactory.getLogger(ErrorHttpHandler.class);

    EncryptoApp app ;

    public ErrorHttpHandler(EncryptoApp app) {
        this.app = app;
    }
        
    public void handle(HttpExchange http, String errorMessage) throws IOException {
        try {
            String path = http.getRequestURI().getPath();
            String errorResponse = JMaps.mapValue("errorMessage", errorMessage).toJson();
            sendResponse(http, "plain/json", errorResponse.getBytes());
        } finally {
            http.close();
        }
    }

    public static void sendResponse(HttpExchange http, String contentType, byte[] bytes) 
            throws IOException {
        http.getResponseHeaders().set("Content-type", contentType);
        http.getResponseHeaders().set("Content-length", Integer.toString(bytes.length));
        http.sendResponseHeaders(HttpURLConnection.HTTP_OK, bytes.length);
        http.getResponseBody().write(bytes);
    }
}
