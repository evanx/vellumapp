/*
 */
package bizstat.http;

import bizstat.server.BizstatServer;
import vellum.httpserver.HttpServerConfig;
import vellum.logr.Logr;
import vellum.logr.LogrFactory;
import vellum.httpserver.VellumHttpServer;

/**
 *
 * @author evan.summers
 */
public class BizstatHttpServer extends VellumHttpServer {
    Logr logger = LogrFactory.getLogger(BizstatHttpServer.class);
    BizstatServer context; 
    HttpServerConfig config;     
    
    public BizstatHttpServer(BizstatServer context, HttpServerConfig config) {
        super(config);
        this.context = context;
    }
    
    public void start() throws Exception {
        super.start(new BizstatHttpHandler(context));
    }   
}
