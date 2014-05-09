/*
 * Source https://github.com/evanx by @evanxsummers

 */
package encrypto.app;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import encrypto.handler.ErrorHttpHandler;
import encrypto.handler.PersonaLogin;
import encrypto.handler.PersonaLogout;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.exception.Exceptions;
import vellum.httphandler.WebHttpHandler;
import vellum.jx.JMap;

/**
 *
 * @author evan.summers
 */
public class WebHttpService implements HttpHandler {

    private final static Logger logger = LoggerFactory.getLogger(WebHttpService.class);
    private final static WebHttpHandler webHandler = new WebHttpHandler("/encrypto/web");
    private final EncryptoApp app;
    private int requestCount = 0;
    private int requestCompletedCount = 0;
    
    public WebHttpService(EncryptoApp app) {
        this.app = app;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        requestCount++;
        String path = httpExchange.getRequestURI().getPath();
        logger.info("handle {}", path);
        Thread.currentThread().setName(path);        
        try {
            app.ensureInitialized();
            if (path.equals("/app/personaLogin")) {
                handle(new PersonaLogin(), new EncryptoHttpx(app, httpExchange));
            } else if (path.equals("/app/personaLogout")) {
                handle(new PersonaLogout(), new EncryptoHttpx(app, httpExchange));
            } else if (path.startsWith("/app/")) {
                    String handlerName = getHandlerName(path);
                    if (handlerName != null) {
                        handle(getHandler(handlerName), new EncryptoHttpx(app, httpExchange));
                    } else {
                        new ErrorHttpHandler(app).handle(httpExchange, "Service not found: " + path);
                    }
                        } else {
                webHandler.handle(httpExchange);
            }
        } catch (Throwable e) {
            String errorMessage = Exceptions.getMessage(e);
            logger.warn("error {} {}", path, errorMessage);
            e.printStackTrace(System.err);
            new ErrorHttpHandler(app).handle(httpExchange, errorMessage);
        } finally {
            requestCompletedCount++;
        }
    }

    private String getHandlerName(String path) {
        int index = path.lastIndexOf("/forwarded");
        if (index > 0) {
            path = path.substring(0, index);
        }
        final String handlerPathPrefix = "/encryptoapp/";
        if (path.startsWith(handlerPathPrefix)) {
            return path.substring(handlerPathPrefix.length());
        }
        return null;
    }

    private EncryptoHttpxHandler getHandler(String handlerName) throws ClassNotFoundException,
            InstantiationException, IllegalAccessException {
        String className = "encrypto.handler.app."
                + Character.toUpperCase(handlerName.charAt(0)) + handlerName.substring(1);
        logger.trace("handler {}", className);
        return (EncryptoHttpxHandler) Class.forName(className).newInstance();
    }

    private void handle(EncryptoHttpxHandler handler, EncryptoHttpx httpx) {
        EncryptoEntityService es = new EncryptoEntityService(app);
        try {
            es.begin();
            JMap responseMap = handler.handle(app, httpx, es);
            logger.trace("response {}", responseMap);
            httpx.sendResponse(responseMap);
            es.commit();
        } catch (Throwable e) {
            httpx.sendError(e);
            es.rollback();
            e.printStackTrace(System.out);
        } finally {
            es.close();
            httpx.close();
        }
    }

    private void handle(PlainHttpxHandler handler, EncryptoHttpx httpx) {
        EncryptoEntityService es = new EncryptoEntityService(app);
        try {
            es.begin();
            String response = handler.handle(app, httpx, es);
            logger.trace("response {}", response);
            httpx.sendPlainResponse(response);
            es.commit();
        } catch (Throwable e) {
            httpx.sendPlainError(String.format("ERROR: %s\n", e.getMessage()));
            es.rollback();
            e.printStackTrace(System.out);
        } finally {
            es.close();
        }
    }
    
    public JMap getMetrics() {
        JMap map = new JMap();
        map.put("requestCount", requestCount);
        map.put("requestCompletedCount", requestCompletedCount);
        return map;
    }
    
}
