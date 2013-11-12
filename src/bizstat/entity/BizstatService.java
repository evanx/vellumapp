/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package bizstat.entity;

import vellum.logr.Logr;
import vellum.logr.LogrFactory;
import bizstat.server.BizstatServer;
import crocserver.storage.service.ServiceType;
import java.util.*;
import vellum.config.ConfigProperties;
import vellum.entity.ConfigurableEntity;
import vellum.type.UniqueList;

/**
 *
 * @author evan.summers
 */
public class BizstatService extends ServiceType implements ConfigurableEntity<BizstatServer> {
    Logr logger = LogrFactory.getLogger(BizstatService.class);
    
    transient Map<String, MetricInfo> metrics = new HashMap();
    transient List<ContactGroup> contactGroupList = new UniqueList();
    
    public BizstatService() {
    }

    public BizstatService(String name) {
        super(name);
    }
        
    public List<ContactGroup> getContactGroupList() {
        return contactGroupList;
    }

    @Override
    public void config(BizstatServer server, ConfigProperties properties) {
        new ServiceConfigurator(server, properties, this).configure();
    }
}
