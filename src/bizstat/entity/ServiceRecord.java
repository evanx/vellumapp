/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package bizstat.entity;

import vellum.util.Args;
import bizstat.enumtype.ServiceStatus;
import java.util.Collection;
import java.util.List;
import vellum.data.Millis;
import vellum.entity.AbstractLongIdEntity;
import vellum.data.Timestamped;
import vellum.parameter.StringMap;
import vellum.util.Strings;

/**
 *
 * @author evan.summers
 */
public class ServiceRecord extends AbstractLongIdEntity implements Timestamped {

    String hostName;
    String serviceName;
    String[] args;
    String outText;
    String errText;
    int exitCode;
    long dispatchedMillis;
    long receivedMillis;
    long notifiedMillis;
    long timestampMillis = System.currentTimeMillis();
    boolean notify;
    transient ServiceStatus serviceStatus;
    transient Throwable exception;
    transient Host host;
    transient BizstatService service;
    transient List<String> outList;
    
    public ServiceRecord() {
    }

    public ServiceRecord(Host host, BizstatService service) {
        this(host.getName(), service.getName());
        this.host = host;
        this.service = service;
    }

    public ServiceRecord(String hostName, String serviceName) {
        this.hostName = hostName;
        this.serviceName = serviceName;
    }
    
    public ServiceRecord(Host host, BizstatService service, long dispatchedMillis) {
        this(host, service);
        this.dispatchedMillis = dispatchedMillis;
    }
    
    public void parseOutText(String outText) {
        this.outText = outText;
        String text = outText.trim();
        int index = text.lastIndexOf("\n");
        if (index > 0) {
            text = text.substring(index + 1);
        }
        if (text.contains("CRITICAL")) {
            serviceStatus = ServiceStatus.CRITICAL;
        } else if (text.contains("OK")) {
            serviceStatus = ServiceStatus.OK;
        } else if (text.contains("WARNING")) {
            serviceStatus = ServiceStatus.WARNING;
        } else {
            serviceStatus = ServiceStatus.UNKNOWN;
        }
    }
    
    public String getHostName() {
        return hostName;
    }

    public String getServiceName() {
        return serviceName;
    }
        
    public HostServiceKey getHostServiceKey() {
        return new HostServiceKey(host, service);
    }

    public Host getHost() {
        return host;
    }
    
    public BizstatService getService() {
        return service;
    }
    
    public String[] getArgs() {
        return args;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }

    public String getErrText() {
        return errText;
    }

    public void setErrText(String errText) {
        this.errText = errText;
    }

    public void setOutList(List<String> outList) {
        this.outList = outList;
        this.outText = Strings.join(" ", outList);
    }

    public List<String> getOutList() {
        return outList;
    }
    
    public String getOutText() {
        return outText;
    }

    public void setOutText(String outText) {
        this.outText = outText;
    }

    public String getMessage() {
        if (outText == null) {
            return null;
        }
        String text = outText.trim();
        int index = text.lastIndexOf("\n");
        if (index > 0) {
            return text.substring(index + 1);
        }
        return text;
    }

    public void setTimestampMillis(long timestampMillis) {
        this.timestampMillis = timestampMillis;
    }
    
    @Override
    public long getTimestamp() {
        return timestampMillis;
    }

    public int getExitCode() {
        return exitCode;
    }

    public void setExitCode(int exitCode) {
        this.exitCode = exitCode;
        if (exitCode == 127) {
            this.serviceStatus = ServiceStatus.ERROR;
        } else if (exitCode == 255) {
            this.serviceStatus = ServiceStatus.ERROR;
        } else if (exitCode < ServiceStatus.NONZERO.ordinal()) {
            this.serviceStatus = ServiceStatus.find(exitCode);
        } else {
            this.serviceStatus = ServiceStatus.NONZERO;
        }
    }

    public void setServiceStatus(ServiceStatus serviceStatus) {
        this.serviceStatus = serviceStatus;
    }

    public ServiceStatus getServiceStatus() {
        return serviceStatus;
    }

    public void setException(Throwable exception) {
        this.exception = exception;
    }

    public Throwable getException() {
        return exception;
    }

    public long getNotifiedMillis() {
        return notifiedMillis;
    }

    public void setNotifiedMillis(long notifiedMillis) {
        this.notifiedMillis = notifiedMillis;
    }

    public long getReceivedMillis() {
        return receivedMillis;
    }

    public void setReceivedMillis(long receivedMillis) {
        this.receivedMillis = receivedMillis;
    }

    public long getDispatchedMillis() {
        return dispatchedMillis;
    }

    public void setDispatchedMillis(long dispatchedMillis) {
        this.dispatchedMillis = dispatchedMillis;
    }

    public boolean isNotify() {
        return notify;
    }

    public void setNotify(boolean notify) {
        this.notify = notify;
    }
    
    public boolean isKnown() {
        return serviceStatus != null && serviceStatus.isKnown();
    }

    public StringMap getStringMap() {
        StringMap map = new StringMap();
        map.put("id", id);
        map.put("hostName", hostName);
        map.put("serviceName", serviceName);
        map.put("serviceStatus", serviceStatus);
        return map;
    }
    
    @Override
    public String toString() {
        return getStringMap().toJson();
    }
        
    public static String toString(Collection<ServiceRecord> collection) {
        return String.format("%d %s", collection.size(), collection.iterator().next());
    }
    
}
