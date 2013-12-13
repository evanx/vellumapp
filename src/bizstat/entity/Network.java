/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package bizstat.entity;

import vellum.logr.Logr;
import vellum.logr.LogrFactory;
import vellum.util.Args;
import bizstat.server.BizstatServer;
import vellum.type.UniqueList;
import java.util.List;
import vellum.config.ConfigProperties;
import vellum.entity.AbstractIdEntity;
import vellum.entity.ConfigurableEntity;

/**
 *
 * @author evan.summers
 */
public class Network extends AbstractIdEntity implements ConfigurableEntity<BizstatServer> {
    static Logr logger = LogrFactory.getLogger(Network.class);
    
    String name;
    String label;
    String description;
    boolean enabled = true;
    transient Host host;
    transient List<Host> hostList = new UniqueList(); 
    transient List<BizstatService> serviceList = new UniqueList(); 
    transient List<ServicePath> servicePathList = new UniqueList();
    transient List<ContactGroup> contactGroupList = new UniqueList();
    
    public Network() {
    }

    public Network(String name) {
        this.name = name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public Comparable getId() {
        return name;
    }

    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Host getHost() {
        return host;
    }

    
    public List<Host> getHostList() {
        return hostList;
    }

    public List<BizstatService> getServiceList() {
        return serviceList;
    }
    
    public List<ServicePath> getServicePathList() {
        return servicePathList;
    }

    public List<ContactGroup> getContactGroupList() {
        return contactGroupList;
    }
    
    @Override
    public String toString() {
        return Args.format(name, host, hostList.size());
    }
    
    @Override
    public void config(BizstatServer server, ConfigProperties properties) {
        label = properties.getString("label", null);
        enabled = properties.getBoolean("enabled", true);
        host = server.getConfigStorage().get(Host.class, properties.getString("host", null));
        logger.info("set network", name, host);
        for (String servicePathName : properties.splitCsv("servicePath")) {
            servicePathList.add(server.getConfigStorage().find(ServicePath.class, servicePathName));
        }
        for (String contactGroupName : properties.splitCsv("contactGroups")) {
            contactGroupList.add(server.getConfigStorage().find(ContactGroup.class, contactGroupName));
        }
    }
    
}
