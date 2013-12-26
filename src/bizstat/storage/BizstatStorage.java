/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package bizstat.storage;

import vellum.connection.ConnectionPool;
import vellum.storage.StorageExceptionType;
import vellum.connection.SimpleConnectionPool;
import vellum.storage.StorageRuntimeException;
import bizstat.storage.servicerecord.ServiceRecordStorage;
import crocserver.storage.common.CrocStorage;
import vellum.data.EntityCache;
import vellum.connection.DataSourceProperties;
import javax.sql.DataSource;
import vellum.data.SimpleEntityCache;
import vellumexp.logr.Logr;
import vellumexp.logr.LogrFactory;

/**
 *
 * @author evan.summers
 */
public class BizstatStorage {

    Logr logger = LogrFactory.getLogger(CrocStorage.class);
    ConnectionPool connectionPool;
    DataSource dataSource;
    EntityCache<String> entityCache;
    
    public BizstatStorage(DataSourceProperties dataSourceInfo) {
        this(new SimpleEntityCache(), new SimpleConnectionPool(dataSourceInfo));

    }
            
    public BizstatStorage(EntityCache typeCache, ConnectionPool connectionPool) {
        this.entityCache = typeCache;
        this.connectionPool = connectionPool;
    }
    
    public void init() throws Exception {
    }
    
    public ServiceRecordStorage getServiceRecordStorage() {
        return new ServiceRecordStorage(this);
    }

    public <T> T getEntity(Class<T> type, String name) {
        T value = entityCache.get(type, name);
        if (value == null) {
            throw new StorageRuntimeException(StorageExceptionType.NOT_FOUND, name);
        }
        return value;
    }

    public ConnectionPool getConnectionPool() {
        return connectionPool;
    }
}
