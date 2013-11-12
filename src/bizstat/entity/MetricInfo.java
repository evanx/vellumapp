/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package bizstat.entity;

import bizstat.enumtype.StatusChangeType;
import bizstat.enumtype.MetricType;
import bizstat.server.BizstatServer;
import java.util.HashMap;
import java.util.Map;
import vellum.config.ConfigProperties;
import vellum.entity.AbstractIdEntity;
import vellum.entity.ConfigurableEntity;

/**
 *
 * @author evan.summers
 */
public class MetricInfo extends AbstractIdEntity implements ConfigurableEntity<BizstatServer> {
    String name;
    String label;
    String description;
    boolean enabled = true;
    MetricType metricType;
    transient BizstatService service;
    transient Map<StatusChangeType, Float> valueMap = new HashMap();

    public MetricInfo() {
    }
        
    public MetricInfo(BizstatService service, String name) {
        this.service = service;
        this.name = name;
    }
    
    @Override
    public Comparable getId() {
        return name;
    }

    @Override
    public String getName() {
        return name;
    }
        
    @Override
    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public MetricType getMetricType() {
        return metricType;
    }

    public void setMetricType(MetricType metricType) {
        this.metricType = metricType;
    }

    public BizstatService getService() {
        return service;
    }

    public void setService(BizstatService service) {
        this.service = service;
    }
    
    public void setValueMap(Map<StatusChangeType, Float> valueMap) {
        this.valueMap = valueMap;
    }

    public Map<StatusChangeType, Float> getValueMap() {
        return valueMap;
    }
            
    @Override
    public void config(BizstatServer server, ConfigProperties properties) {
        String serviceName = properties.getString("service", name);
        service = server.getConfigStorage().find(BizstatService.class, serviceName);
        valueMap = StatusChangeType.newValueMap(properties.splitCsv("values"));
    }
}
