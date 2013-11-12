/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package bizstat.entity;

import bizstat.enumtype.NotifyType;
import bizstat.enumtype.StatusChangeType;
import bizstat.enumtype.MetricType;
import bizstat.server.BizstatServer;
import vellum.datatype.Millis;
import java.util.Date;
import java.util.List;
import vellum.config.ConfigProperties;
import vellum.logr.Logr;
import vellum.logr.LogrFactory;

/**
 *
 * @author evan.summers
 */
public class ServiceConfigurator {
    Logr logger = LogrFactory.getLogger(ServiceConfigurator.class);
    BizstatService service;
    BizstatServer server;
    ConfigProperties properties;

    public ServiceConfigurator(BizstatServer server, ConfigProperties properties, BizstatService service) {
        this.server = server;
        this.properties = properties;
        this.service = service;
    }
    
    public void configure() {
        service.setLabel(properties.getString("label", null));
        service.setEnabled(properties.getBoolean("enabled", true));
        service.setHost(server.getConfigStorage().get(Host.class, properties.getString("host", null)));
        for (String contactGroupName : properties.splitCsv("contactGroups")) {    
            service.contactGroupList.add(server.getConfigStorage().find(ContactGroup.class, contactGroupName));
        } 
        service.setNotifyType(properties.getEnum("notifyType", NotifyType.class, NotifyType.STATUS_CHANGED));
        service.setScheduleTime(properties.getTime("scheduleTime", null));
        if (service.getScheduleTime() != null) {
            if (service.getNotifyType() == null) {
                service.setNotifyType(NotifyType.ALWAYS);
            }
            service.setIntervalMillis(properties.getMillis("interval", Millis.fromDays(1)));
            if (false) {
                long millis = System.currentTimeMillis(); 
                millis -= millis % Millis.fromMinutes(10);
                millis += Millis.fromMinutes(10);
                service.setScheduleTime(new Date(millis));
            }
        } else {
            service.setIntervalMillis(properties.getMillis("interval", server.getConfig().getIntervalMillis()));
            if (service.getNotifyType() == null) {
                service.setNotifyType(NotifyType.STATUS_CHANGED);
            } else if (service.getNotifyType() != NotifyType.STATUS_CHANGED) {
                service.setNotifyMillis(properties.getMillis("notify", service.getIntervalMillis()));
            }
            service.getRepeatCountMap().putAll(server.getConfig().getRepeatCountMap());
            List<String> repeatCounts = properties.splitCsv("repeatCounts");
            if (!repeatCounts.isEmpty()) {
                service.getRepeatCountMap().putAll(StatusChangeType.newIntegerMap(repeatCounts));
            }
            service.getNotifyIntervalMap().putAll(server.getConfig().getNotifyIntervalMap());
            List<String> notifyIntervals = properties.splitCsv("notifyIntervals");
            if (!notifyIntervals.isEmpty()) {
                service.getNotifyIntervalMap().putAll(StatusChangeType.newIntervalMap(notifyIntervals));
            }
            configureRepeatCount();
        }
        if (properties.containsKey("values")) {
            MetricInfo metricInfo = new MetricInfo(service, service.getName());
            metricInfo.setMetricType(properties.getEnum("metricType", MetricType.class, MetricType.FLOAT));
            metricInfo.setValueMap(StatusChangeType.newValueMap(properties.splitCsv("values")));
        }
        for (String metricName : properties.splitCsv("metrics")) {
            MetricInfo metricInfo = server.getConfigStorage().get(MetricInfo.class, metricName);
            if (metricInfo == null) {
                metricInfo = new MetricInfo(service, metricName);
            }
            service.metrics.put(metricName, metricInfo);
        }
    }
    
    private void configureRepeatCount() {
        for (StatusChangeType notifyEventType : service.getNotifyIntervalMap().keySet()) {
            Integer repeatCount = service.getRepeatCountMap().get(notifyEventType);
            if (repeatCount != null) {
                Long notifyInterval = service.getNotifyIntervalMap().get(notifyEventType);
                if (notifyInterval != null) {
                    int minimumRepeatCount = (int) (notifyInterval / service.getIntervalMillis());
                    if (repeatCount < minimumRepeatCount) {
                        service.getRepeatCountMap().put(notifyEventType, minimumRepeatCount);
                    }
                    logger.verbose("configureRepeatCount", notifyEventType, repeatCount, notifyEventType, minimumRepeatCount);
                }
            }
        }
    }
}
