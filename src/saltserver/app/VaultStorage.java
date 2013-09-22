/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package saltserver.app;

import vellum.storage.DataSourceConfig;
import javax.sql.DataSource;
import saltserver.storage.adminuser.AdminUserStorage;
import saltserver.storage.schema.VaultSchema;
import saltserver.storage.secret.SecretStorage;
import vellum.logr.Logr;
import vellum.logr.LogrFactory;
import vellum.storage.*;

/**
 *
 * @author evan.summers
 */
public class VaultStorage {

    Logr logger = LogrFactory.getLogger(VaultStorage.class);
    ConnectionPool connectionPool;
    DataSource dataSource;
    
    public VaultStorage(DataSourceConfig dataSourceInfo) {
        this(new SimpleConnectionPool(dataSourceInfo));

    }
            
    public VaultStorage(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }
    
    public void init() throws Exception {
        new VaultSchema(this).verifySchema();
        getSecretStorage().validate();
    }

    public AdminUserStorage getAdminUserStorage() {
        return new AdminUserStorage(this);
    }
    
    public SecretStorage getSecretStorage() {
        return new SecretStorage(this);
    }
       
    public ConnectionPool getConnectionPool() {
        return connectionPool;
    }
}
