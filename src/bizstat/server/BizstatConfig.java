/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package bizstat.server;

import bizstat.enumtype.StatusChangeType;
import vellumexp.logr.Logr;
import vellumexp.logr.LogrFactory;
import vellum.system.Systems;
import vellum.data.TimePeriod;
import java.io.File;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import vellum.config.ConfigException;
import vellum.config.ConfigExceptionType;
import vellum.config.ConfigProperties;
import vellum.httpserver.HttpServerProperties;
import vellum.connection.DataSourceProperties;

/**
 *
 * @author evan.summers
 */
public class BizstatConfig extends AbstractConfig {
    
    Collection<String> networks = properties.splitCsv("networks");
    boolean run = properties.getBoolean("run", false);
    boolean exec = properties.getBoolean("exec", false);
    boolean h2 = properties.getBoolean("h2", false);
    boolean verbose = properties.getBoolean("verbose", false);
    Date startTime = properties.getTime("startTime", null);
    Date stopTime = properties.getTime("stopTime", null);
    long processTimeoutMillis = properties.getMillis("processTimeout");
    long intervalMillis = properties.getMillis("interval");
    long sleepMillis = properties.getMillis("sleep", intervalMillis);
    long notifyMillis = properties.getMillis("notifyInterval");
    long escalateMillis = properties.getMillis("escalateInterval");
    int outputSize = properties.getInt("outputSize", 64000);
    String adminContact = properties.getString("adminContact", null);
    String checkScript = properties.getString("checkScript");
    String notifyScript = properties.getString("notifyScript");
    String smtpServer = properties.getString("smtpServer", "localhost");
    String smsPrefix = properties.getString("smsPrefix", null);
    String homeDir = properties.getString("homeDir", "bizstat/");
    String confDir = properties.getString("confDir", "conf/");
    String logDir = properties.getString("logDir", "log/");
    String scriptsDir = properties.getString("homeDir", "scripts/");
    TimePeriod notifyPeriod = new TimePeriod(properties.getString("notifyPeriod", "06:00-22:30"));
    boolean embeddedH2database = true;
            
    Logr logger = LogrFactory.getLogger(BizstatConfig.class);
    BizstatServer server;
    
    Map<StatusChangeType, Integer> repeatCountMap = new HashMap();
    Map<StatusChangeType, Long> notifyIntervalMap = new HashMap();
    int threadPoolSize = 10;
    DataSourceProperties dataSourceConfig;
    HttpServerProperties httpServerConfig;
    
    public BizstatConfig(BizstatServer server) {
        super(server.configProperties);
        this.server = server;
        notifyIntervalMap.putAll(StatusChangeType.newIntervalMap(
                properties.splitCsv("notifyIntervals")));
        repeatCountMap.putAll(StatusChangeType.newIntegerMap(
                properties.splitCsv("repeatCounts")));
        logger.info("interval", intervalMillis);
        logger.info("sleep", sleepMillis);
        initDataSourceConfig();
        initHttpServerInfoConfig();
    }

    private void initHttpServerInfoConfig() {
        String httpServerName = properties.getString("httpServer");
        if (httpServerName != null) {
            ConfigProperties props = 
                    server.configMap.find("HttpServer", httpServerName).getProperties();
            httpServerConfig = new HttpServerProperties(props);
        }
    }
    
    private void initDataSourceConfig() {
        String dataSource = properties.getString("dataSource");
        if (dataSource != null) {
            dataSourceConfig = new DataSourceProperties(
                    server.configMap.find("DataSource", dataSource).getProperties());
        }
    }
    
    public void init() {
        logger.info("userDir", Systems.userDir);
        logger.info("networks", networks);
        verify();
    }

    public void verify() {
        verifyDir(scriptsDir);
        verifyExecutable(checkScript);
        verifyExecutable(notifyScript);
    }

    private void verifyDir(String dir) {
        if (!new File(dir).isDirectory()) {
            throw new IllegalArgumentException(dir);
        }
    }

    private void verifyExecutable(String script) {
        if (!new File(script).exists()) {
            throw new ConfigException(ConfigExceptionType.NOT_EXISTS, script);
        }
        if (run && !new File(script).canExecute()) {
            throw new ConfigException(ConfigExceptionType.NOT_EXECUTABLE, script);
        }
    }

    public long getIntervalMillis() {
        return intervalMillis;
    }

    public Map<StatusChangeType, Integer> getRepeatCountMap() {
        return repeatCountMap;
    }

    public Map<StatusChangeType, Long> getNotifyIntervalMap() {
        return notifyIntervalMap;
    }
    
    public String getCheckScript() {
        return checkScript;
    }
    
    public boolean isRun() {
        return run;
    }

    public boolean isExec() {
        return exec;
    }

    public boolean isH2() {
        return h2;
    }
        
    public int getOutputSize() {
        return outputSize;
    }

    public DataSourceProperties getDataSourceInfo() {
        return dataSourceConfig;
    }

    public HttpServerProperties getHttpServerConfig() {
        return httpServerConfig;
    } 
}
