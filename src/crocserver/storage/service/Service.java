/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package crocserver.storage.service;

import java.security.cert.X509Certificate;
import java.util.Date;
import vellum.entity.AbstractIdEntity;
import vellumcert.Pems;

/**
 *
 * @author evan.summers
 */
public final class Service extends AbstractIdEntity {
    long orgId;
    Long id;
    String hostName;
    String serviceName;
    String cert;
    String subject;
    boolean enabled = true;
    Date inserted = new Date();
    Date updated = new Date();
    String updatedBy;
    boolean stored = false;
    
    transient String orgName;
    
    public Service() {
    }

    public Service(long orgId, String hostName, String serviceName, String updatedBy) {
        this.orgId = orgId;
        this.hostName = hostName;
        this.serviceName = serviceName;
        this.updatedBy = updatedBy;
    }
    
    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getOrgId() {
        return orgId;
    }

    public void setOrgId(long orgId) {
        this.orgId = orgId;
    }
        
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getCert() {
        return cert;
    }

    public void setCert(String cert) {
        this.cert = cert;
    }

    public void setX509Cert(X509Certificate x509Cert) {
        this.cert = Pems.buildCertPem(x509Cert);
        this.subject = x509Cert.getSubjectDN().getName();
    }
    
    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getSubject() {
        return subject;
    }
    
    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public void setStored(boolean stored) {
        this.stored = stored;
    }

    public boolean isStored() {
        return stored;
    }
        
    @Override
    public String toString() {
        return getId().toString();
    }
}
