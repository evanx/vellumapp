/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package saltserver.storage.secret;

import java.util.Date;
import vellum.entity.AbstractIdEntity;

/**
 *
 * @author evan.summers
 */
public class Secret extends AbstractIdEntity {
    Long id;
    String group;
    String name;
    String secret;
    boolean stored = false;
            
    public Secret() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }
    
    public void setStored(boolean stored) {
        this.stored = stored;
    }
    
    public boolean isStored() {
        return stored;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }
           
    @Override
    public String toString() {
        return getId().toString();
    }
    
}
