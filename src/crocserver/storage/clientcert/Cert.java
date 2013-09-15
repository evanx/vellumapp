/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package crocserver.storage.clientcert;

import java.security.cert.X509Certificate;
import java.util.Date;
import vellum.entity.AbstractIdEntity;
import vellum.security.Certificates;
import vellum.security.Pems;

/**
 *
 * @author evan.summers
 */
public final class Cert extends AbstractIdEntity {
    Long id;
    Long orgId;
    String name;
    String subject;
    String cert;
    String ipAddress;
    boolean enabled = true;
    Date inserted = new Date();
    Date updated = new Date();
    boolean stored = false;

    public Cert() {
    }
    
    public void setCert(X509Certificate x509Cert) {
        this.cert = Pems.buildCertPem(x509Cert);
        this.subject = x509Cert.getSubjectDN().getName();
        this.name = Certificates.getCommonName(subject);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
        
    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getCert() {
        return cert;
    }

    public void setCert(String cert) {
        this.cert = cert;
    }
    
    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getSubject() {
        return subject;
    }
    
    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public Long getOrgId() {
        return orgId;
    }
    
    public void setStored(boolean stored) {
        this.stored = stored;
    }

    public boolean isStored() {
        return stored;
    }
        
    @Override
    public String toString() {
        return subject;
    }    
}
