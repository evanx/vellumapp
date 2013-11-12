/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package bizstat.server;

import vellum.config.ConfigProperties;

/**
 *
 * @author evan.summers
 */
public class AbstractConfig {
    protected ConfigProperties properties;

    public AbstractConfig(ConfigProperties properties) {
        this.properties = properties;
    }
        
}
