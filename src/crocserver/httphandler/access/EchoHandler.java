/*
 * Source https://github.com/evanx by @evanxsummers
 */
package crocserver.httphandler.access;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import crocserver.app.CrocApp;
import vellum.httpserver.Httpx;
import java.io.IOException;
import java.io.PrintStream;
import vellum.datatype.Millis;
import vellum.jx.JMap;
import vellum.logr.Logr;
import vellum.logr.LogrFactory;

/**
 *
 * @author evan.summers
 */
public class EchoHandler implements HttpHandler {
    
    Logr logger = LogrFactory.getLogger(getClass());
    CrocApp app;
    HttpExchange httpExchange;
    Httpx httpExchangeInfo;
    PrintStream out;

    public EchoHandler(CrocApp app) {
        super();
        this.app = app;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        logger.info("handle", getClass().getSimpleName());
        this.httpExchange = httpExchange;
        httpExchangeInfo = new Httpx(httpExchange);
        try {
            out = new PrintStream(httpExchange.getResponseBody());
            JMap cookieMap = new JMap();
            String cookieKey = "testCookieKey";
            cookieMap.put(cookieKey, "testValue");
            cookieMap.put("testCookieKey1", "testValue1");
            cookieMap.put("testCookieKey2", "testValue2");
            httpExchangeInfo.setCookie(cookieMap, Millis.fromHours(24));
            httpExchangeInfo.sendResponse("text/plain", true);
            out.println(httpExchange.getRequestURI().toString());
            out.printf("cookie %s: %s\n", cookieKey, 
                    httpExchangeInfo.getCookieMap().getString(cookieKey));
            Headers reqHeaders = httpExchange.getRequestHeaders();
            for (String key : reqHeaders.keySet()) {
                out.printf("request header %s: %d: %s\n", key, 
                        reqHeaders.get(key).size(), reqHeaders.get(key).get(0));
            }
            Headers resHeaders = httpExchange.getResponseHeaders();
            for (String key : resHeaders.keySet()) {
                out.printf("response header %s: %s\n", key, resHeaders.get(key));
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            httpExchangeInfo.handleError(e);
        }
        httpExchange.close();
    }
}
