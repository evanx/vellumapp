/*
 * Apache Software License 2.0, (c) Copyright 2012 Evan Summers
 * 
 */
package saltserver.app;

import dualcontrol.ExtendedProperties;
import vellum.httpserver.HttpsServerConfig;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.net.URLConnection;
import org.h2.tools.Server;
import saltserver.crypto.AESCipher;
import vellum.config.ConfigMap;
import vellum.config.ConfigParser;
import vellum.config.ConfigProperties;
import vellum.logr.Logr;
import vellum.logr.LogrFactory;
import vellum.logr.LogrLevel;
import vellum.storage.DataSourceConfig;
import vellum.storage.SimpleConnectionPool;
import vellum.util.Streams;
import vellum.httpserver.VellumHttpsServer;
import vellum.security.DefaultKeyStores;

/**
 *
 * @author evan.summers
 */
public class VaultApp {

    Logr logger = LogrFactory.getLogger(getClass());
    VaultStorage storage;
    ConfigMap configMap;
    ConfigProperties configProperties;
    DataSourceConfig dataSourceConfig;
    Thread serverThread;
    String confFileName;
    Server h2Server;
    VellumHttpsServer httpsServer;
    AESCipher cipher; 
    VaultPasswordManager passwordManager = new VaultPasswordManager();
    
    public void init() throws Exception {
        initConfig();
        sendShutdown();
        if (configProperties.getBoolean("startH2TcpServer")) {
            h2Server = Server.createTcpServer().start();
        }
        dataSourceConfig = new DataSourceConfig(configMap.get("DataSource",
                configProperties.getString("dataSource")).getProperties());
        storage = new VaultStorage(new SimpleConnectionPool(dataSourceConfig));
        storage.init();
        String httpsServerConfigName = configProperties.getString("httpsServer");
        if (httpsServerConfigName != null) {
            ExtendedProperties props = new ExtendedProperties(
                    configMap.find("HttpsServer", httpsServerConfigName).getProperties());
            HttpsServerConfig httpsServerConfig = new HttpsServerConfig(props);
            if (httpsServerConfig.isEnabled()) {
                httpsServer = new VellumHttpsServer();
                httpsServer.start(props, new VaultHttpHandler(this));
            }
        }
    }

    private void initConfig() throws Exception {
        confFileName = getString("salt.conf");
        File confFile = new File(confFileName);
        logger.info("conf", confFileName, confFile);
        configMap = ConfigParser.parse(new FileInputStream(confFile));
        configProperties = configMap.find("Config", "default").getProperties();
        String logLevelName = configProperties.getString("logLevel");
        if (logLevelName != null) {
            LogrFactory.setDefaultLevel(LogrLevel.valueOf(logLevelName));
        }
    }
    public void sendShutdown() {
        String shutdownUrl = configProperties.getString("shutdownUrl");
        logger.info("sendShutdown", shutdownUrl);
        try {
            URL url = new URL(shutdownUrl);
            URLConnection connection = url.openConnection();
            String response = Streams.readString(connection.getInputStream());
            connection.getInputStream().close();
            logger.info(response);
        } catch (Exception e) {
            logger.warn(e.getMessage());
        }
    }

    public void stop() throws Exception {
        if (httpsServer != null) {
            httpsServer.shutdown();
        }
        if (h2Server != null) {
            h2Server.stop();
        }
    }

    private String getString(String name) {
        String string = System.getProperty(name);
        if (string == null) {
            throw new RuntimeException(name);
        }
        return string;
    }

    public VaultStorage getStorage() {
        return storage;
    }

    public void setCipher(AESCipher cipher) {
        this.cipher = cipher;
    }
    
    public AESCipher getCipher() {
        return cipher;
    }

    public VaultPasswordManager getPasswordManager() {
        return passwordManager;
    }
        
    public static void main(String[] args) throws Exception {
        try {
            VaultApp app = new VaultApp();
            app.init();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }
}
