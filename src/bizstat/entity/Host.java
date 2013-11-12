/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package bizstat.entity;

import vellum.logr.Logr;
import vellum.logr.LogrFactory;
import vellum.util.Args;
import bizstat.server.BizstatServer;
import crocserver.storage.org.Org;
import vellum.type.UniqueList;
import java.util.List;
import vellum.config.ConfigProperties;
import vellum.entity.AbstractIdEntity;
import vellum.entity.ConfigurableEntity;

/**
 *
 * @author evan.summers
 */
public class Host extends AbstractIdEntity implements ConfigurableEntity<BizstatServer> {

    static Logr logger = LogrFactory.getLogger(Host.class);
    
    String name;
    String fullName;
    String ipNumber;
    boolean enabled;
    long orgId;
    Org org;
    
    transient Network network;    
    transient List<BizstatService> serviceList = new UniqueList();
    transient List<ContactGroup> contactGroupList = new UniqueList();

    public Host() {
    }

    public Host(String name) {
        this.name = name;
    }

    @Override
    public Comparable getId() {
        return name;
    }
    
    @Override
    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public String getName() {
        return name;
    }

    public Org getOrg() {
        return org;
    }

    public void setOrg(Org org) {
        this.org = org;
    }

    public long getOrgId() {
        return orgId;
    }

    public void setOrgId(long orgId) {
        this.orgId = orgId;
    }
    
    public String getIpNumber() {
        return ipNumber;
    }

    public void setIpNumber(String ipNumber) {
        this.ipNumber = ipNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }
    
    public Network getHostGroup() {
        return network;
    }

    public List<BizstatService> getServiceList() {
        return serviceList;
    }

    public Network getNetwork() {
        return network;
    }
    
    public List<ContactGroup> getContactGroupList() {
        return contactGroupList;
    }
    
    @Override
    public void config(BizstatServer server, ConfigProperties properties) {
        ipNumber = properties.getString("ipNumber", null);
        enabled = properties.getBoolean("enabled", true);
        network = server.getConfigStorage().find(Network.class, properties.getString("network"));
        network.getHostList().add(this);
        for (String serviceName : properties.splitCsv("services")) {
            BizstatService service = server.getConfigStorage().find(BizstatService.class, serviceName);
            logger.info("services add", name, serviceName);
            serviceList.add(service);
        }
       for (String contactGroupName : properties.splitCsv("contactGroups")) {
            contactGroupList.add(server.getConfigStorage().find(ContactGroup.class, contactGroupName));
        }
     }
    
    @Override
    public String toString() {
        return Args.format(name);
    }
}
