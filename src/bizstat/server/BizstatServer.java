/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package bizstat.server;

import bizstat.entity.BizstatConfigStorage;
import bizstat.entity.BizstatService;
import bizstat.entity.BizstatStorageHtmlPrinter;
import bizstat.entity.Contact;
import bizstat.entity.Host;
import bizstat.entity.HostServiceKey;
import bizstat.entity.HostServiceStatus;
import bizstat.entity.Network;
import bizstat.entity.ServicePath;
import bizstat.entity.ServiceRecord;
import bizstat.http.BizstatHttpServer;
import bizstat.enumtype.ServiceStatus;
import bizstat.http.BizstatTypeCache;
import bizstat.storage.BizstatStorage;
import crocserver.storage.org.Org;
import java.sql.SQLException;
import vellum.logr.Logr;
import vellum.logr.LogrFactory;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import org.h2.tools.Server;
import vellum.config.ConfigMap;
import vellum.config.ConfigProperties;
import vellum.exception.Exceptions;
import vellum.storage.ConnectionPool;
import vellum.storage.SimpleConnectionPool;

/**
 *
 * @author evan.summers
 */
public class BizstatServer implements Runnable {

    Logr logger = LogrFactory.getLogger(BizstatServer.class);
    ConfigMap configMap;
    ConfigProperties configProperties;
    BizstatConfig config;
    BizstatConfigStorage configStorage;
    BizstatStorage storage;
    BizstatHttpServer httpServer;
    BizstatGtalkConnection gtalk;
    boolean stopped = false;
    List<Network> networkList = new ArrayList();
    List<Host> hostList;
    List<ServicePath> servicePathList;
    List<BizstatService> serviceList;
    long startMillis = System.currentTimeMillis();
    long dispatcherMillis;
    long notifiedMillis;
    Map<HostServiceKey, HostServiceStatus> statusMap = new ConcurrentHashMap();
    ScheduledExecutorService scheduledExecutorService;
    Contact adminContact;
    Set<Contact> adminContacts = new TreeSet();
    Server h2Server;
    Org org = new Org("default");
    
    public BizstatServer() {
        org.setId(1);
    }

    public void init(ConfigMap configMap, ConfigProperties configProperties) 
            throws Exception {
        this.configMap = configMap;
        this.configProperties = configProperties;
        config = new BizstatConfig(this);
        config.init();
        if (config.isH2()) {
            h2Server = Server.createTcpServer().start();    
        }
        if (config.getDataSourceInfo() != null && config.getDataSourceInfo().isEnabled()) {
            ConnectionPool connectionPool = new SimpleConnectionPool(config.getDataSourceInfo());
            storage = new BizstatStorage(new BizstatTypeCache(this), connectionPool);
            storage.init();
        }
        configStorage = new BizstatConfigStorage(this);
        configStorage.init(configMap);
        adminContact = configStorage.find(Contact.class, config.adminContact);
        if (config.verbose) {
            new BizstatStorageHtmlPrinter(this).print(System.out);
        }
        for (String networkName : config.networks) {
            networkList.add(configStorage.find(Network.class, networkName));
        }
        if (!configMap.getList("Gtalk").isEmpty() && configMap.get("Gtalk", "default") != null && 
                configMap.get("Gtalk", "default").getProperties().getBoolean("enabled")) {
            gtalk = new BizstatGtalkConnection(this);
            gtalk.connect();
        }
        hostList = configStorage.getExtentList(Host.class);
        servicePathList = configStorage.getExtentList(ServicePath.class);
        serviceList = configStorage.getExtentList(BizstatService.class);
        logger.info("init", hostList.size());
        scheduledExecutorService = Executors.newScheduledThreadPool(config.threadPoolSize);
        test();
    }
    
    public void start() throws Exception {
        if (config.getHttpServerConfig() != null && config.getHttpServerConfig().isEnabled()) {
            httpServer = new BizstatHttpServer(this, config.httpServerConfig);
            httpServer.start();
        }  
    }
    
    void test() throws SQLException {
        HostServiceKey key = new HostServiceKey(hostList.get(0), serviceList.get(0));
        HostServiceStatus status = getStatus(key);
        ServiceRecord serviceRecord = new ServiceRecord(key.getHost(), key.getService(), System.currentTimeMillis());
        serviceRecord.setServiceStatus(ServiceStatus.UNKNOWN);
        serviceRecord.setTimestampMillis(System.currentTimeMillis());
        serviceRecord.setExitCode(2);
        insert(serviceRecord);
    }
    
    public void sendAdminMessage(String message) {
        if (gtalk != null && !isStopped()) {
            try {
                gtalk.sendMessage(adminContact, message);
            } catch (Exception e) {
                logger.warn(e, "sendAdminMessage", message);
            }
        }
    }

    public void shutdown(boolean restarting) {
        scheduledExecutorService.shutdown();
        if (!restarting && h2Server != null) {
            h2Server.stop();
        }
        if (gtalk != null) {
            gtalk.close();
        }
        if (httpServer != null) {
            httpServer.stop();
        }
        logger.info("stopped");
    }

    public ScheduledExecutorService getScheduledExecutorService() {
        return scheduledExecutorService;
    }

    public void setStopped(boolean stopped) {
        this.stopped = stopped;
    }

    public boolean isStopped() {
        return stopped;
    }

    @Override
    public void run() {
        sendAdminMessage("Started " + getClass().getSimpleName());
        new BizstatScheduler(this).run();
        while (!stopped) {
            dispatcherMillis = System.currentTimeMillis();
            new BizstatDispatcher(this).run();
            if (dispatcherMillis - notifiedMillis < config.notifyMillis) {
                logger.warn("ignore notifyMillis", dispatcherMillis - notifiedMillis);
            } else {
                runNotifier();
            }
        }
    }

    private synchronized void runNotifier() {
        new BizstatNotifier(this).run();
    }

    public HostServiceStatus getStatus(HostServiceKey key) {
        HostServiceStatus status = statusMap.get(key);
        if (status == null) {
            status = new HostServiceStatus(this, key.getHost(), key.getService());
            statusMap.put(status.getHostServiceKey(), status);
        }
        return status;
    }

    public synchronized void setServiceRecord(ServiceRecord serviceRecord) {
        HostServiceStatus status = getStatus(serviceRecord.getHostServiceKey());
        status.setServiceRecord(serviceRecord);
        logger.info("setserviceRecord", serviceRecord);
    }

    public BizstatStorage getStorage() {
        return storage;
    }

    public BizstatConfigStorage getConfigStorage() {
        return configStorage;
    }

    public BizstatConfig getConfig() {
        return config;
    }

    public ConfigMap getConfigMap() {
        return configMap;
    }

    public ConfigProperties getConfigProperties() {
        return configProperties;
    }

    public long getDispatcherMillis() {
        return dispatcherMillis;
    }

    public Set<Contact> getAdminContacts() {
        return adminContacts;
    }

    public Map<HostServiceKey, HostServiceStatus> getStatusMap() {
        return statusMap;
    }

    public List<BizstatService> getServiceList() {
        return serviceList;
    }

    public void insert(ServiceRecord serviceRecord) {
        try {
            storage.getServiceRecordStorage().insert(org, serviceRecord);
        } catch (SQLException e) {
            throw Exceptions.newRuntimeException(e);
        }
    }

        
}
