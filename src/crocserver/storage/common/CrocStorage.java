/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package crocserver.storage.common;

import vellum.connection.ConnectionPool;
import vellum.storage.StorageExceptionType;
import vellum.connection.SimpleConnectionPool;
import vellum.storage.StorageRuntimeException;
import crocserver.storage.schema.CrocSchema;
import crocserver.storage.adminuser.AdminUserStorage;
import crocserver.storage.clientcert.CertStorage;
import crocserver.storage.servicerecord.ServiceRecordStorage;
import vellum.data.EntityCache;
import vellum.connection.DataSourceProperties;
import javax.sql.DataSource;
import vellum.data.SimpleEntityCache;
import vellumexp.logr.Logr;
import vellumexp.logr.LogrFactory;
import crocserver.storage.schema.SchemaPrinter;
import crocserver.storage.org.OrgStorage;
import crocserver.storage.orgrole.OrgRoleStorage;
import crocserver.storage.service.ServiceStorage;

/**
 *
 * @author evan.summers
 */
public class CrocStorage {

    Logr logger = LogrFactory.getLogger(CrocStorage.class);
    ConnectionPool connectionPool;
    DataSource dataSource;
    EntityCache<String> entityCache;
    
    public CrocStorage(DataSourceProperties dataSourceInfo) {
        this(new SimpleEntityCache(), new SimpleConnectionPool(dataSourceInfo));

    }
            
    public CrocStorage(EntityCache typeCache, ConnectionPool connectionPool) {
        this.entityCache = typeCache;
        this.connectionPool = connectionPool;
    }
    
    public void init() throws Exception {
        new CrocSchema(this).verifySchema();
        new SchemaPrinter().handle(connectionPool, System.out, "PUBLIC");
        getUserStorage().validate();
    }

    public AdminUserStorage getUserStorage() {
        return new AdminUserStorage(this);
    }

    public OrgStorage getOrgStorage() {
        return new OrgStorage(this);
    }
       
    public CertStorage getCertStorage() {
        return new CertStorage(this);
    }

    public ServiceStorage getServiceStorage() {
        return new ServiceStorage(this);
    }

    public OrgRoleStorage getOrgRoleStorage() {
        return new OrgRoleStorage(this);
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
