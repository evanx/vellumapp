/*
 */
package bizstat.http;

import bizstat.server.BizstatServer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import vellum.logr.Logr;
import vellum.logr.LogrFactory;

/**
 *
 * @author evan.summers
 */
public class BizstatHttpHandler implements HttpHandler {
    
    Logr logger = LogrFactory.getLogger(BizstatHttpHandler.class);
    BizstatServer context; 
    
    public BizstatHttpHandler(BizstatServer context) {
        this.context = context;
    }    

    @Override
    public void handle(HttpExchange he) throws IOException {
        logger.info("he", he.getRequestURI().toString());
        new BizstatHomePageHandler(context).handle(he);    
    }
    
}
