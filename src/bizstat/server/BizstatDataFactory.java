/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package bizstat.server;

import bizstat.entity.BizstatConfigStorage;

/**
 *
 * @author evan.summers
 */
public interface BizstatDataFactory {
    public void init(BizstatConfigStorage storage);
    
}
