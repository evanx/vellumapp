/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package bizstat.entity;

import bizstat.enumtype.NotifyType;
import bizstat.enumtype.StatusChangeType;
import vellum.logr.Logr;
import vellum.logr.LogrFactory;
import vellum.util.Args;
import bizstat.server.BizstatServer;
import crocserver.storage.org.Org;
import crocserver.storage.service.ServiceType;
import java.util.*;
import vellum.config.PropertiesStringMap;
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
    public void config(BizstatServer server, PropertiesStringMap properties) {
        new ServiceConfigurator(server, properties, this).configure();
    }    
}
