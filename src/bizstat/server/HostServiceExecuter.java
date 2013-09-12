/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package bizstat.server;

import vellum.util.Lists;
import vellum.util.Streams;
import bizstat.enumtype.ServiceStatus;
import java.io.InputStream;
import vellum.logr.Logr;
import vellum.logr.LogrFactory;

/**
 *
 * @author evan.summers
 */
public class HostServiceExecuter {
        
    Logr logger = LogrFactory.getLogger(HostServiceExecuter.class);
    BizstatServer server;
    BizstatConfig config;
    HostServiceKey key;
    ServiceRecord serviceRecord;
    
    public HostServiceExecuter(BizstatServer server, Host host, BizstatService service) {
        this.server = server;
        this.config = server.getConfig();
        this.key = key;
        this.serviceRecord = new ServiceRecord(host, service, server.dispatcherMillis);
    }

    public ServiceRecord execute() {
        return exec(config.getCheckScript(), key.getService().getName(), key.getHost().getName());
    }

    private ServiceRecord exec(String... args) {
        logger.verbose("exec", Lists.format(args));
        serviceRecord.setArgs(args);
        if (config.isExec()) {
            try {
                Process process = Runtime.getRuntime().exec(args);
                InputStream inputStream = process.getInputStream();
                InputStream errorStream = process.getErrorStream();
                serviceRecord.setExitCode(process.waitFor());
                serviceRecord.setOutList(Streams.readLineList(inputStream, config.getOutputSize()));
                serviceRecord.setErrText(Streams.readString(errorStream));
                logger.verbose("exec", serviceRecord.getExitCode(), serviceRecord.getServiceStatus());
                if (serviceRecord.getOutText().length() > 0) {
                }
            } catch (Exception e) {
                logger.warn(e, null);
                serviceRecord.setServiceStatus(ServiceStatus.INDETERMINATE);
            }
        } else {
            serviceRecord.setServiceStatus(ServiceStatus.DISABLED);
        }
        serviceRecord.setTimestampMillis(System.currentTimeMillis());
        return serviceRecord;
    }
}

