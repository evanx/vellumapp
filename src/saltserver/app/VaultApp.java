/*
 * Apache Software License 2.0, (c) Copyright 2012 Evan Summers
 * 
 */
package saltserver.app;

import vellum.httpserver.HttpServerConfig;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.net.URLConnection;
import org.h2.tools.Server;
import saltserver.crypto.AESCipher;
import vellum.config.ConfigMap;
import vellum.config.ConfigParser;
import vellum.config.PropertiesStringMap;
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
    DataSourceConfig dataSourceConfig;
    PropertiesStringMap configProperties;
    Thread serverThread;
    String confFileName;
    ConfigMap configMap;
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
            HttpServerConfig httpsServerConfig = new HttpServerConfig(
                    configMap.find("HttpsServer", httpsServerConfigName).getProperties());
            if (httpsServerConfig.isEnabled()) {
                httpsServer = new VellumHttpsServer(httpsServerConfig);
                httpsServer.init(DefaultKeyStores.createSSLContext());
            }
        }
    }

    private void initConfig() throws Exception {
        confFileName = getString("salt.conf");
        File confFile = new File(confFileName);
        logger.info("conf", confFileName, confFile);
        configMap = ConfigParser.parse(new FileInputStream(confFile));
        configProperties = configMap.find("Config", "default").getProperties();
        String logLevelName = configProperties.get("logLevel");
        if (logLevelName != null) {
            LogrFactory.setDefaultLevel(LogrLevel.valueOf(logLevelName));
        }
    }

    public void start() throws Exception {
        if (httpsServer != null) {
            httpsServer.start();
            httpsServer.startContext("/", new VaultHttpHandler(this));
            logger.info("HTTPS server started");
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
            httpsServer.stop();
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
            app.start();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }
}
