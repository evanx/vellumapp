/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package bizstat.http;

import com.sun.net.httpserver.HttpHandler;

/**
 *
 * @author evan.summers
 */
public interface HttpHandlerFactory {
    public HttpHandler createHttpHandler(String context);
}
