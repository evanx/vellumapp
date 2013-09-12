/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package bizstat.http;

import bizstat.server.BizstatServer;
import vellum.datatype.EntityCache;

/**
 *
 * @author evan.summers
 */
public class BizstatTypeCache implements EntityCache<String> {
    BizstatServer server;

    public BizstatTypeCache(BizstatServer server) {
        this.server = server;
    }
    
    public <V> V get(Class<V> type, String name) {
        return server.getConfigStorage().get(type, name);
    }

}
