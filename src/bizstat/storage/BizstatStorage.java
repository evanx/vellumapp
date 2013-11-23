/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package bizstat.storage;

import vellum.storage.ConnectionPool;
import vellum.storage.StorageExceptionType;
import vellum.storage.SimpleConnectionPool;
import vellum.storage.StorageRuntimeException;
import bizstat.storage.servicerecord.ServiceRecordStorage;
import crocserver.storage.common.CrocStorage;
import vellum.datatype.EntityCache;
import vellum.storage.DataSourceConfig;
import javax.sql.DataSource;
import vellum.datatype.SimpleEntityCache;
import vellum.logr.Logr;
import vellum.logr.LogrFactory;

/**
 *
 * @author evan.summers
 */
public class BizstatStorage {

    Logr logger = LogrFactory.getLogger(CrocStorage.class);
    ConnectionPool connectionPool;
    DataSource dataSource;
    EntityCache<String> entityCache;
    
    public BizstatStorage(DataSourceConfig dataSourceInfo) {
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
