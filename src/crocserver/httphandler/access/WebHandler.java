/*
 * Source https://github.com/evanx by @evanxsummers
 */
package crocserver.httphandler.access;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import crocserver.app.CrocApp;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;
import vellum.logr.Logr;
import vellum.logr.LogrFactory;
import vellum.util.Streams;
import vellum.util.Strings;

/**
 *
 * @author evan.summers
 */
public class WebHandler implements HttpHandler {

    Logr logger = LogrFactory.getLogger(WebHandler.class);
    CrocApp app;
    Map<String, byte[]> cache = new HashMap();
    final String resourceNamePrefix = "/crocserver/web";
    
    public WebHandler(CrocApp app) {
        this.app = app;
    }

    public void init() throws IOException {
        loadReplace("/local.js");
        loadReplace("/pindex.html");
        loadReplace("/pindex.js");
        loadReplace("/bindex.html");
        loadReplace("/bindex.js");
    }
    
    public void loadReplace(String path) throws IOException {
        InputStream resourceStream = getClass().getResourceAsStream(resourceNamePrefix + path);
        StringBuilder text = Streams.readStringBuilder(resourceStream);
        replace(text);
        byte[] bytes = text.toString().getBytes();
        cache.put(path, bytes);
    }

    public void load(String path) throws IOException {
        InputStream resourceStream = getClass().getResourceAsStream(resourceNamePrefix + path);
        StringBuilder text = Streams.readStringBuilder(resourceStream);
        replace(text);
        byte[] bytes = text.toString().getBytes();
        cache.put(path, bytes);
    }
    
    private void replace(StringBuilder text) {
        Strings.replace(text, "${loginUrl}", app.getGoogleApi().getLoginUrl());
        Strings.replace(text, "${clientId}", app.getGoogleApi().getClientId());
        Strings.replace(text, "${redirectUrl}", app.getGoogleApi().getRedirectUrl());
        Strings.replace(text, "${serverUrl}", app.getServerUrl());
        Strings.replace(text, "${secureUrl}", app.getSecureUrl());
    }
    
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String path = httpExchange.getRequestURI().getPath();
        if (httpExchange.getRequestURI().getPath().endsWith(".png")) {
            httpExchange.getResponseHeaders().set("Content-type", "image/png");
        } else if (httpExchange.getRequestURI().getPath().endsWith(".html")) {
            httpExchange.getResponseHeaders().set("Content-type", "text/html");
        } else if (httpExchange.getRequestURI().getPath().endsWith(".css")) {
            httpExchange.getResponseHeaders().set("Content-type", "text/css");
        } else if (httpExchange.getRequestURI().getPath().endsWith(".js")) {
            httpExchange.getResponseHeaders().set("Content-type", "text/javascript");
        } else if (httpExchange.getRequestURI().getPath().endsWith(".txt")) {
            httpExchange.getResponseHeaders().set("Content-type", "text/plain");
        } else if (httpExchange.getRequestURI().getPath().endsWith(".html")) {
            httpExchange.getResponseHeaders().set("Content-type", "text/html");
        } else {
            httpExchange.getResponseHeaders().set("Content-type", "text/html");
            path = app.getHomePage();
        }
        if (!path.startsWith("/bootstrap")) {
            logger.info("path", path);
        }
        try {
            byte[] bytes = cache.get(path);
            if (bytes == null) {
                InputStream resourceStream = getClass().getResourceAsStream(resourceNamePrefix + path);
                bytes = Streams.readBytes(resourceStream);
                cache.put(path, bytes);
            }
            logger.info("path", path, bytes.length);
            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
            httpExchange.getResponseBody().write(bytes);
        } catch (IOException e) {
            logger.warn(e);
            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, 0);
        }
        httpExchange.close();
    }
}
