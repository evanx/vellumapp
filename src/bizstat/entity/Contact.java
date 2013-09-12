/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package bizstat.entity;

import vellum.entity.AbstractIdEntity;
import java.util.Date;
import vellum.config.ConfigEntry;
import vellum.config.PropertiesStringMap;
import vellum.util.Args;

/**
 *
 * @author evan.summers
 */
public class Contact extends AbstractIdEntity {
    String name; 
    String fullName;
    String email;
    String im;
    String sms;
    Date notifyTime;
    boolean admin = false;
    boolean enabled = true;
    boolean gtalk = true;
    transient ContactGroup contactGroup;
    
    public Contact() {
    }

    public Contact(ConfigEntry entry) {
        this(entry.getName(), entry.getProperties());
    }
    
    public Contact(String name, PropertiesStringMap props) {
        this.name = name;
        this.fullName = props.getString("fullName");
        this.email = props.getString("email");
        this.im = props.getString("im");
        this.enabled = props.getBoolean("enabled");
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIm() {
        return im;
    }

    public boolean isGtalk() {
        return gtalk;
    }
    
    public void setIm(String im) {
        this.im = im;
    }
    
    public String getSms() {
        return sms;
    }

    public void setSms(String sms) {
        this.sms = sms;
    }

    public Date getNotifyTime() {
        return notifyTime;
    }

    public void setNotifyTime(Date notifyTime) {
        this.notifyTime = notifyTime;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    @Override
    public Comparable getId() {
        return name;
    }

    public ContactGroup getContactGroup() {
        return contactGroup;
    }

    public PropertiesStringMap getPropertiesMap() {
        PropertiesStringMap map = new PropertiesStringMap();
        map.put("name", name);
        map.put("fullName", fullName);
        map.put("sms", sms);
        map.put("email", email);
        return map;
    }
    
    @Override
    public String toString() {
        return Args.format(name, im, contactGroup);
    }    
}
