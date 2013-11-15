/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package bizstat.server;

import bizstat.enumtype.StatusChangeType;
import vellum.httpserver.HttpsServerConfig;
import vellum.logr.Logr;
import vellum.logr.LogrFactory;
import vellum.util.Systems;
import vellum.datatype.TimePeriod;
import java.io.File;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import vellum.config.ConfigException;
import vellum.config.ConfigExceptionType;
import vellum.config.ConfigProperties;
import vellum.lifecycle.Initialisable;
import vellum.storage.DataSourceConfig;

/**
 *
 * @author evan.summers
 */
public class BizstatConfig extends AbstractConfig implements Initialisable {
    
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
    DataSourceConfig dataSourceConfig;
    HttpsServerConfig httpServerConfig;
    
    public BizstatConfig(BizstatServer server) {
        super(server.configProperties);
        this.server = server;
        notifyIntervalMap.putAll(StatusChangeType.newIntervalMap(properties.splitCsv("notifyIntervals")));
        repeatCountMap.putAll(StatusChangeType.newIntegerMap(properties.splitCsv("repeatCounts")));
        logger.info("interval", intervalMillis);
        logger.info("sleep", sleepMillis);
        initDataSourceConfig();
        initHttpServerInfoConfig();
    }

    private void initHttpServerInfoConfig() {
        String httpServerName = properties.getString("httpServer");
        if (httpServerName != null) {
            ConfigProperties props = server.configMap.find("HttpServer", httpServerName).getProperties();
            httpServerConfig = new HttpsServerConfig(props);
        }
    }
    
    private void initDataSourceConfig() {
        String dataSource = properties.getString("dataSource");
        if (dataSource != null) {
            dataSourceConfig = new DataSourceConfig(server.configMap.find("DataSource", dataSource).getProperties());
        }
    }
    
    @Override
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

    public DataSourceConfig getDataSourceInfo() {
        return dataSourceConfig;
    }

    public HttpsServerConfig getHttpServerConfig() {
        return httpServerConfig;
    } 
}
