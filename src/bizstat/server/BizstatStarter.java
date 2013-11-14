/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package bizstat.server;

import bizstat.filewatcher.DirWatcherListener;
import bizstat.filewatcher.DirWatcherTask;
import java.io.File;
import java.io.FileInputStream;
import java.util.Collection;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import vellum.config.ConfigMap;
import vellum.config.ConfigParser;
import vellum.config.ConfigProperties;
import vellum.config.ConfigEntry;
import vellum.logr.Logr;
import vellum.logr.LogrFactory;
import vellum.logr.LogrLevel;


    
/**
 *
 * @author evan.summers
 */
public class BizstatStarter implements Runnable, DirWatcherListener {
    static final String CONFIG_FILE_EXTENSION = ".conf";
    static final String MAIN_CONFIG_FILE = "bizstat.conf";
    static final String CONTACTS_CONFIG_FILE = "contacts.conf";
    
    Logr logger = LogrFactory.getLogger(BizstatStarter.class);
    ConfigMap configMap;
    ConfigProperties configProperties;
    BizstatServer server;
    Thread serverThread;
    
    String confDirName = "conf/";
    File confDir;
    
    DirWatcherTask watcher = new DirWatcherTask();    
        
    public BizstatStarter() {
    }

    private void init() throws Exception {
        confDir = new File(confDirName);
        try {
            initConfigMap();
            server = new BizstatServer();
            server.init(configMap, configProperties);
            server.start();
            configMap = null;
            configProperties = null;
            if (server.getConfig().isRun()) {
                watcher.init(confDir, CONFIG_FILE_EXTENSION, this);
                Executors.newScheduledThreadPool(4).scheduleAtFixedRate(watcher, 2000, 500, TimeUnit.MILLISECONDS);
                serverThread = new Thread(server);
                serverThread.start();
            } else {
                server.test();
                Thread.sleep(8000);
                server.shutdown(false);
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }  
    }
    
    @Override
    public void dirChanged(DirWatcherTask watcher, Collection<File> files) {
        logger.info("dirChanged");
        try {
            server.sendAdminMessage("Changed: " + files.iterator().next().getName());
            initConfigMap();
            BizstatServer newServer = new BizstatServer();
            newServer.init(configMap, configProperties);
            logger.info("stop");
            server.setStopped(true);    
            serverThread.join();    
            server.shutdown(true);
            server = newServer;
            server.start();
            serverThread = new Thread(server);
            logger.info("start");
            serverThread.start();
        } catch (Exception e) {
            server.sendAdminMessage("Config error: " + e.getMessage());
            logger.warn(e, null);
            return;
        }
    }
    
    @Override
    public void run() {
    }
    
    private void initConfigMap() throws Exception {
        configMap = ConfigParser.parse(new FileInputStream(
                new File(confDirName, MAIN_CONFIG_FILE)));
        ConfigMap contactsConfigMap = ConfigParser.parse(new FileInputStream(
                new File(confDirName, CONTACTS_CONFIG_FILE)));
        configMap.putAll(contactsConfigMap);
        configProperties = configMap.find("Config", "default").getProperties();
        String logLevelName = configProperties.getString("logLevel");
        if (logLevelName != null) {
            LogrFactory.setDefaultLevel(LogrLevel.valueOf(logLevelName));
        }
        String profile = System.getProperty("bizstat.profile");
        configProperties.putAll(configMap.find("Config", profile).getProperties());
        for (ConfigEntry network : configMap.getList("Network")) {
            File networkFile = new File(confDirName, network.getName() + CONFIG_FILE_EXTENSION);
            if (networkFile.exists()) {
                ConfigMap networkConfigMap = ConfigParser.parse(new FileInputStream(networkFile));
                for (ConfigEntry entry : networkConfigMap.getEntryList()) {
                    entry.getProperties().put("network", network.getName());
                }
                configMap.putAll(networkConfigMap);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        BizstatStarter starter = new BizstatStarter();
        starter.init();
        starter.run();
    }

}
