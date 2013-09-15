/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package bizstat.server;

import bizstat.entity.BizstatService;
import bizstat.entity.Host;
import bizstat.entity.HostServiceKey;
import bizstat.entity.HostServiceStatus;
import bizstat.entity.Network;
import bizstat.entity.ServicePath;
import bizstat.entity.ServiceRecord;
import vellum.datatype.Millis;
import vellum.logr.Logr;
import vellum.logr.LogrFactory;
import bizstat.enumtype.ServiceStatus;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author evan.summers
 */
public class BizstatNetworkDispatcher implements Runnable {

    Logr logger = LogrFactory.getLogger(BizstatNetworkDispatcher.class);
    BizstatServer server;
    Network network;
    Set<HostServiceKey> statusKeySet = new TreeSet();

    public BizstatNetworkDispatcher(BizstatServer server, Network network) {
        this.server = server;
        this.network = network;
    }

    @Override
    public void run() {
        logger.info("run", network, Millis.formatAsSeconds(server.dispatcherMillis));
        checkServicePaths(network.getServicePathList());
        checkHosts(network.getHostList());
    }

    private void checkServicePaths(Collection<ServicePath> servicePaths) {
        logger.verbose("checkServiceGroups", servicePaths.size());
        for (ServicePath serviceGroup : servicePaths) {
            if (serviceGroup.isEnabled() && !server.isStopped()) {
                checkServicePath(serviceGroup);
            }
        }
    }

    private void checkServicePath(ServicePath servicePath) {
        boolean ok = true;
        for (BizstatService service : servicePath.getServiceList()) {
            if (service.isEnabled() && service.getHost().isEnabled() && !server.isStopped()) {
                Host host = service.getHost();
                HostServiceKey key = new HostServiceKey(host, service);
                HostServiceStatus status = server.getStatus(key);
                if (!statusKeySet.contains(key)) {
                    if (ok) {
                        status.executeServiceRecord();
                        if (!status.getServiceStatus().isOk()) {
                            ok = false;
                        }
                    } else {
                        logger.info("checkServicePath blocked", service);
                        ServiceRecord statusRecord = new ServiceRecord(host, service, server.dispatcherMillis);
                        statusRecord.setServiceStatus(ServiceStatus.BLOCKED);
                        server.setServiceRecord(statusRecord);
                    }
                    statusKeySet.add(key);
                } else {
                    logger.info("checkServicePath skip", service);
                }
            }
        }
    }

    private void checkHosts(Collection<Host> hosts) {
        logger.verbose("checkHosts", hosts.size());
        for (Host host : hosts) {
            if (host.isEnabled() && !server.isStopped()) {
                checkHost(host);
            }
        }
    }

    private void checkHost(Host host) {
        logger.verbose("checkHost", host);
        for (BizstatService service : host.getServiceList()) {
            HostServiceKey key = new HostServiceKey(host, service);
            if (!statusKeySet.contains(key) && !server.isStopped()) {
                try {
                    checkHostService(host, service);
                } catch (Exception e) {
                    logger.warn(e, "checkHost", host, service);
                    ServiceRecord statusRecord = new ServiceRecord(host, service, server.dispatcherMillis);
                    statusRecord.setServiceStatus(ServiceStatus.INDETERMINATE);
                    statusRecord.setException(e);
                    server.setServiceRecord(statusRecord);
                }
                statusKeySet.add(key);
            } else {
                logger.info("checkHost ignore", host, service);
            }
        }
    }

    private boolean checkHostService(Host host, BizstatService service) {
        if (service.isEnabled() && service.getScheduleTime() == null && !server.isStopped()) {
            HostServiceKey key = new HostServiceKey(host, service);
            HostServiceStatus status = server.getStatus(key);
            long dispatchedThresholdMillis = server.dispatcherMillis - service.getIntervalMillis();
            if (status.getDispatchedMillis() < dispatchedThresholdMillis) {
                status.executeServiceRecord();
                return true;
            } else {
                logger.info("skip interval", host, service, dispatchedThresholdMillis, service.getIntervalMillis());
            }
        }
        return false;
    }
}