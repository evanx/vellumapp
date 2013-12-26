/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package bizstat.entity;

import bizstat.enumtype.NotifyType;
import bizstat.enumtype.StatusChangeType;
import vellum.util.Args;
import bizstat.enumtype.ServiceStatus;
import bizstat.server.BizstatConfig;
import bizstat.server.BizstatServer;
import bizstat.server.HostServiceExecuter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import vellumexp.logr.Logr;
import vellumexp.logr.LogrFactory;
import vellum.data.UniqueList;
import vellum.util.Strings;

/**
 *
 * @author evan.summers
 */
public class HostServiceStatus implements Runnable {

    public static final int MAX_REPEAT_COUNT = 99;
    Logr logger = LogrFactory.getLogger(HostServiceStatus.class);
    BizstatServer server;
    BizstatConfig config;
    int repeatCount = MAX_REPEAT_COUNT;
    long dispatchedMillis;
    long changedMillis;
    long notifiedMillis;
    long receivedMillis;
    Host host;
    BizstatService service;
    ServiceStatus serviceStatus;
    ServiceStatus notifiedServiceStatus;
    StatusChangeType statusChangeType;
    ServiceRecord serviceRecord;
    ServiceRecord notifiedServiceRecord;
    ServiceRecord previousServiceRecord;
    List<ContactGroup> contactGroupList = new UniqueList();
    ScheduledFuture scheduledFuture;
    boolean outputChanged = false;
    transient HostServiceKey hostServiceKey; 

    public HostServiceStatus(BizstatServer server, HostServiceKey hostServiceKey) {
        this.server = server;
        this.config = server.getConfig();
        this.hostServiceKey = hostServiceKey;
        this.host = hostServiceKey.getHost();
        this.service = hostServiceKey.getService();
        contactGroupList.addAll(host.getNetwork().getContactGroupList());
        contactGroupList.addAll(host.getContactGroupList());
        contactGroupList.addAll(service.getContactGroupList());
    }

    public HostServiceStatus(BizstatServer server, Host host, BizstatService service) {
        this(server, new HostServiceKey(host, service));
    }

    public List<ContactGroup> getContactGroupList() {
        return contactGroupList;
    }

    public void schedule(long initialDelay, long period) {
        this.scheduledFuture = server.getScheduledExecutorService().
                scheduleAtFixedRate(this, initialDelay, period, TimeUnit.MILLISECONDS);
    }

    @Override
    public void run() {
        executeServiceRecord();
    }

    public void executeServiceRecord() {
        server.setServiceRecord(new HostServiceExecuter(server, host, service).execute());
    }

    public void setNotifiedServiceRecord(ServiceRecord notifiedServiceRecord) {
        this.notifiedServiceRecord = notifiedServiceRecord;
        this.notifiedServiceStatus = notifiedServiceRecord.getServiceStatus();
        this.notifiedMillis = notifiedServiceRecord.getNotifiedMillis();
    }

    public ServiceStatus getNotifiedServiceStatus() {
        return notifiedServiceStatus;
    }

    public long getReceivedMillis() {
        return receivedMillis;
    }

    public long getNotifiedMillis() {
        return notifiedMillis;
    }

    public void setNotifiedMillis(long notifiedMillis) {
        this.notifiedServiceRecord = serviceRecord;
        this.notifiedServiceStatus = serviceStatus;
        this.notifiedMillis = notifiedMillis;
    }

    public long getChangedMillis() {
        return changedMillis;
    }

    public void setChangedMillis(long changedMillis) {
        this.changedMillis = changedMillis;
    }

    public long getDispatchedMillis() {
        return dispatchedMillis;
    }

    public void setDispatchedMillis(long dispatchedMillis) {
        this.dispatchedMillis = dispatchedMillis;
    }

    public Host getHost() {
        return host;
    }

    public BizstatService getService() {
        return service;
    }

    public ServiceStatus getServiceStatus() {
        return serviceStatus;
    }

    public ServiceRecord getServiceRecord() {
        return serviceRecord;
    }

    public int getRepeatCount() {
        return repeatCount;
    }

    public StatusChangeType getStatusChangeType() {
        return statusChangeType;
    }

    private void setServiceStatus(ServiceStatus serviceStatus) {
        if (this.serviceStatus == null) {
            if (statusChangeType != null) {
                logger.warn("setServiceStatus notifyEventType");
            }
        } else if (serviceStatus == null) {
            statusChangeType = null;
            logger.warn("setServiceStatus");
        } else if (this.serviceStatus != serviceStatus) {
            statusChangeType = serviceStatus.getStatusChangeType(this.serviceStatus);
            if (statusChangeType == null) {
                logger.warn("setServiceStatus statusChangeType");
                statusChangeType = StatusChangeType.OK_WARNING;
            }
            changedMillis = System.currentTimeMillis();
            repeatCount = 0;
        } else if (repeatCount < MAX_REPEAT_COUNT) {
            repeatCount++;
        }
        this.serviceStatus = serviceStatus;
    }

    public void setServiceRecord(ServiceRecord serviceRecord) {
        previousServiceRecord = this.serviceRecord;
        receivedMillis = System.currentTimeMillis();
        if (service.getNotifyType() == NotifyType.OUTPUT_CHANGED) {
            outputChanged = false;
            if (previousServiceRecord != null && !Strings.equals(previousServiceRecord.getOutList(), serviceRecord.getOutList())) {
                outputChanged = true;
                serviceRecord.setServiceStatus(ServiceStatus.WARNING);
            } else {
                serviceRecord.setServiceStatus(ServiceStatus.OK);
            }
        }
        if (serviceRecord.getServiceStatus() == null) {
            serviceRecord.setServiceStatus(ServiceStatus.UNKNOWN);
        }
        setServiceStatus(serviceRecord.getServiceStatus());
        this.serviceRecord = serviceRecord;
        parseMetrics();
    }

    public boolean isOutputChanged() {
        return outputChanged;
    }
    
    private void parseMetrics() {
        Map<String, String> metrics = new HashMap();
        for (String line : serviceRecord.getOutList()) {
            if (line.startsWith("value ")) {
                logger.info("parseMetrics", line);
                String[] words = line.split(" ");
                if (words.length == 3) {
                    metrics.put(words[1], words[2]);
                }
            }
        }
        if (!metrics.isEmpty()) {
            setMetrics(metrics);
        }
    }

    private void setMetrics(Map<String, String> metrics) {
        logger.verbose("setMetrics", service.metrics.keySet());
        for (MetricInfo metric : service.metrics.values()) {
            String valueString = metrics.get(metric.getName());
            if (valueString != null) {
                try {
                    float value = Float.parseFloat(valueString);
                    logger.info("setserviceRecord value", valueString, value);
                } catch (NumberFormatException pe) {
                    logger.warn(pe, "parseMetrics", metric.getName(), valueString);
                }
            }
        }
    }

    public boolean isNotify() {
        if (receivedMillis > server.getDispatcherMillis() && serviceStatus != null && serviceStatus.isKnown()) {
            if (service.getNotifyType() == NotifyType.OUTPUT_CHANGED) {
                return outputChanged;
            } else if (service.getNotifyType() == NotifyType.ALWAYS) {
                if (service.getScheduleTime() != null) {
                    return true;
                }
            } else if (service.getNotifyType() == NotifyType.NOT_OK) {
                if (serviceStatus != ServiceStatus.OK) {
                    if (notifiedMillis < server.getDispatcherMillis() - service.getNotifyMillis()) {
                        return true;
                    } else {
                        logger.info("ignore notifyMillis", service, service.getNotifyMillis(),
                                server.getDispatcherMillis() - notifiedMillis);
                    }
                }
            } else if (service.getNotifyType() == NotifyType.STATUS_CHANGED) {
                if (statusChangeType != null) {
                    Long notifyInterval = service.getNotifyIntervalMap().get(statusChangeType);
                    if (notifyInterval == null) {
                        logger.info("ignore notifyInterval null", service, statusChangeType);
                    } else if (notifyInterval.longValue() == 0) {
                        logger.info("ignore notifyInterval zero", service, statusChangeType);
                    } else if (repeatCount >= service.getRepeatCountMap().get(statusChangeType)) {
                        if (notifiedServiceStatus == null) {
                            notifiedServiceStatus = serviceStatus;
                            logger.info("initial", service, serviceStatus, statusChangeType, repeatCount);
                        } else if (serviceStatus != notifiedServiceStatus) {
                            return true;
                        }
                    } else {
                        logger.info("ignore repeatCount", service, serviceStatus, statusChangeType, repeatCount);
                    }
                } else {
                    logger.info("ignore statusChangeType", service, serviceStatus, statusChangeType);
                }
            } else {
                logger.warn("ignore notifyType", service.getNotifyType());
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return Args.format(host, service, serviceStatus, notifiedServiceStatus);
    }

    public HostServiceKey getHostServiceKey() {
        return hostServiceKey;
    }
}
