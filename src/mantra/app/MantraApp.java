/*
 * Apache Software License 2.0, (c) Copyright 2012 Evan Summers
 * 
 */
package mantra.app;

import ephemeral.EphemeralClientSSLContextFactory;
import vellum.util.ExtendedProperties;
import vellum.httpserver.HttpsServerConfig;
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
import vellum.util.Systems;

/**
 *
 * @author evan.summers
 */
public class MantraApp {

    Logr logger = LogrFactory.getLogger(getClass());
    MantraStorage storage;
    DataSourceConfig dataSourceConfig;
    ConfigMap configMap;
    ConfigProperties configProperties;
    Thread serverThread;
    String confFileName;
    Server h2Server;
    VellumHttpsServer httpsServer;
    AESCipher cipher; 
    MantraPasswordManager passwordManager = new MantraPasswordManager();
    String keyAlias;
    String keyStoreLocation;

    public void init() throws Exception {
        initConfig();
        sendShutdown();
        if (configProperties.getBoolean("startH2TcpServer")) {
            h2Server = Server.createTcpServer().start();
        }
        dataSourceConfig = new DataSourceConfig(configMap.get("DataSource",
                configProperties.getString("dataSource")).getProperties());
        storage = new MantraStorage(new SimpleConnectionPool(dataSourceConfig));
        storage.init();
        String httpsServerConfigName = configProperties.getString("httpsServer");
        if (httpsServerConfigName != null) {
            ExtendedProperties props = new ExtendedProperties(
                    configMap.find("HttpsServer", httpsServerConfigName).getProperties());
            HttpsServerConfig httpsServerConfig = new HttpsServerConfig(props);
            if (httpsServerConfig.isEnabled()) {
                httpsServer = new VellumHttpsServer();
                httpsServer.start(props, new EphemeralClientSSLContextFactory(),
                        new MantraHttpHandler(this));
            }
        }
        keyAlias = configProperties.getString("keyAlias");
        keyStoreLocation = Systems.getPath(configProperties.getString("keyStore"));
    }

    private void initConfig() throws Exception {
        configMap = ConfigParser.parseConfFile(System.getProperty("mantra.conf"));
        configProperties = configMap.find("Config", "default").getProperties();
        String logLevelName = configProperties.getString("logLevel");
        if (logLevelName != null) {
            LogrFactory.setDefaultLevel(LogrLevel.valueOf(logLevelName));
        }
    }

    public void start() throws Exception {
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

    public MantraStorage getStorage() {
        return storage;
    }

    public void setCipher(AESCipher cipher) {
        this.cipher = cipher;
    }
    
    public AESCipher getCipher() {
        return cipher;
    }

    public String getKeyAlias() {
        return keyAlias;
    }

    public String getKeyStoreLocation() {
        return keyStoreLocation;
    }
        
    public MantraPasswordManager getPasswordManager() {
        return passwordManager;
    }

    public static void main(String[] args) throws Exception {
        try {
            MantraApp app = new MantraApp();
            app.init();
            app.start();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }
}
