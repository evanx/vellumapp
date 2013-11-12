/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package bizstat.entity;

import bizstat.server.BizstatServer;
import vellum.config.ConfigProperties;

/**
 *
 * @author evan.summers
 */
public class ContactConfigurator {
    
    public void config(Contact contact, BizstatServer server, ConfigProperties properties) {
        contact.fullName = properties.getString("fullName", null);
        contact.sms = properties.getString("sms", null);
        contact.email = properties.getString("email", null);
        contact.im = properties.getString("gtalk", null);
        contact.enabled = properties.getBoolean("enabled", false);
        contact.admin = properties.getBoolean("admin", false);
        if (contact.admin) {
            server.getAdminContacts().add(contact);
        }
    }
    
}
