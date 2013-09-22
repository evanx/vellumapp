/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package venigma.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import vellum.logr.Logr;
import vellum.logr.LogrFactory;
import vellum.util.RowSets;
import venigma.server.CipherConfig;
import venigma.server.CipherContext;
import venigma.server.CipherProperties;
import venigma.server.storage.DatabaseConnectionInfo;
import venigma.server.storage.IdEntityMap;
import venigma.server.storage.PairMap;
import venigma.server.storage.VStorageExceptionType;
import venigma.server.storage.VStorageRuntimeException;

/**
 *
 * @author evan.summers
 */
public class CipherStorage {

    Logr logger = LogrFactory.getLogger(CipherStorage.class);
    IdEntityMap<KeyInfo> keyInfoStorage = new IdEntityMap();
    PairMap<KeyInfoAdminUserPair> KeyInfoAdminUserPairStorage = new PairMap();
    PairMap<AdminUserPair> adminUserPairStorage = new PairMap();
    CipherContext context;
    CipherConfig config;
    CipherProperties properties;
    DatabaseConnectionInfo databaseConnectionInfo;
    
    public CipherStorage(CipherContext context) {
        this.context = context;
    }

    public void init() throws Exception {
        config = context.getConfig();
        databaseConnectionInfo = config.databaseConnectionInfo;
        properties = context.getProperties();
        Class.forName(config.databaseConnectionInfo.getDriver());
        if (properties.databaseStorePassword != null) {
            initEncryptedDatabase();
        }
        Connection connection = getConnection();
        new VenigmaSchemaStorage(connection).verifySchema();
        releaseConnection(connection);
    }

    private void initEncryptedDatabase() throws Exception {
    }
    
    public Connection getConnection() {
        try {
            return DriverManager.getConnection(databaseConnectionInfo.getUrl(),
                    databaseConnectionInfo.getUsername(), properties.buildDatabasePassword());
        } catch (SQLException e) {
            throw new VStorageRuntimeException(VStorageExceptionType.CONNECTION_ERROR, e);
        }
    }

    public void releaseConnection(Connection connection) {
        RowSets.close(connection);
    }

    public AdminUserStorage getAdminUserStorage() {
        return new AdminUserStorage(getConnection());
    }
    
    public KeyInfoStorage getKeyInfoStorage() {
        return new KeyInfoStorage(getConnection());
    }

    
}
