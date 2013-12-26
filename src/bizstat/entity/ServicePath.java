/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package bizstat.entity;

import vellumexp.logr.Logr;
import vellumexp.logr.LogrFactory;
import vellum.util.Args;
import bizstat.server.BizstatServer;
import vellum.data.UniqueList;
import java.util.List;
import vellum.config.ConfigProperties;
import vellum.entity.AbstractIdEntity;
import vellum.entity.ConfigurableEntity;

/**
 *
 * @author evan.summers
 */
public class ServicePath extends AbstractIdEntity implements ConfigurableEntity<BizstatServer> {
    static Logr logger = LogrFactory.getLogger(ServicePath.class);
    
    String name;
    String label;
    boolean enabled = true;

    transient Network network;
    transient List<BizstatService> serviceList = new UniqueList();
    transient List<ContactGroup> contactGroupList = new UniqueList();
    
    public ServicePath() {
    }

    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public List<BizstatService> getServiceList() {
        return serviceList;
    }

    public List<ContactGroup> getContactGroupList() {
        return contactGroupList;
    }

    public boolean isEnabled() {
        return enabled;
    }
    
    @Override
    public Comparable getId() {
        return name;
    }
    
    @Override
    public String toString() {
        return Args.format(name, serviceList);
    }

    @Override
    public void config(BizstatServer server, ConfigProperties properties) {
        label = properties.getString("label", null);
        enabled = properties.getBoolean("enabled", true);
        network = server.getConfigStorage().find(Network.class, properties.getString("network"));
        network.getServicePathList().add(this);
        for (String serviceName : properties.splitCsv("services")) {
            BizstatService service = server.getConfigStorage().find(BizstatService.class, serviceName);
            logger.info("services add", name, serviceName);
            serviceList.add(service);
        }
    }
    
}
