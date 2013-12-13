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
import vellum.entity.AbstractIdEntity;
import java.util.List;
import vellum.config.ConfigProperties;
import vellum.entity.ConfigurableEntity;

/**
 *
 * @author evan.summers
 */
public class ContactGroup extends AbstractIdEntity implements ConfigurableEntity<BizstatServer> {
    static Logr logger = LogrFactory.getLogger(ContactGroup.class);

    String name;
    String label;
    boolean enabled;
    transient List<Contact> contactList = new UniqueList();
    
    public ContactGroup() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public List<Contact> getContactList() {
        return contactList;
    }

    @Override
    public Comparable getId() {
        return name;
    }

    @Override
    public String toString() {
        return Args.format(name);
    }

    @Override
    public void config(BizstatServer server, ConfigProperties properties) {
        label = properties.getString("label", null);
        enabled = properties.getBoolean("enabled");
        for (String contactName : properties.splitCsv("contacts")) {
            Contact contact = server.getConfigStorage().find(Contact.class, contactName);
            logger.info("contact", name, contactName);
            contactList.add(contact);
        }  
    }
    
}
