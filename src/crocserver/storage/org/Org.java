/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package crocserver.storage.org;

import java.util.Date;
import vellum.datatype.Patterns;
import vellum.entity.AbstractIdEntity;
import vellum.jx.JMap;
import vellum.jx.JMapException;
import vellum.security.Certificates;
import vellum.validation.ValidationException;
import vellum.validation.ValidationExceptionType;

/**
 *
 * @author evan.summers
 */
public final class Org extends AbstractIdEntity<Long> {
    Long id;
    String orgName;
    String displayName;
    String url;
    String region;
    String locality;
    String country;
    boolean enabled = true;
    Date updated;
    boolean stored = false;
            
    public Org() {
    }

    public Org(String orgName) {
        this.orgName = orgName;
    }
      
    public Org(JMap map) throws JMapException {
        update(map);
    }

    public void update(Org bean) throws JMapException {
        update(bean.getMap());
    }
    
    public void update(JMap map) throws JMapException {
        url = map.getString("url");
        orgName = map.getString("orgName", null);
        displayName = map.getString("displayName", null);
        region = map.getString("region", null);
        locality = map.getString("locality", null);
        country = map.getString("country", null);
        if (orgName == null) {
            orgName = url;
        }
        if (displayName == null) {
            displayName = orgName;
        }
    }
    
    public JMap getMap() {
        JMap map = new JMap();
        map.put("orgId", id);
        map.put("orgName", orgName);
        map.put("displayName", displayName);
        map.put("url", url);
        map.put("region", region);
        map.put("locality", locality);
        map.put("country", country);
        map.put("enabled", enabled);
        return map;
    }
    
    @Override
    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
    
    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String formatDname(String cn, String ou) {
        return Certificates.formatDname(cn, ou, orgName, locality, region, country);
    }
    
    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public void setStored(boolean stored) {
        this.stored = stored;
    }
    
    public boolean isStored() {
        return stored;
    }

    public void validate() throws ValidationException {
        if (!Patterns.matchesUrl(url)) {
            throw new ValidationException(ValidationExceptionType.INVALID_URL, url);
        }
    }

    public String toJson() {
        return getMap().toJson();
    }
    
    @Override
    public String toString() {
        return getMap().toJson();
    }
}
