/*
 * Source https://github.com/evanx by @evanxsummers
 */
package bizstat.entity;

import vellum.entity.EntityMap;
import vellum.entity.Matcher;
import vellum.entity.ConfigurableEntity;
import vellum.entity.IdEntity;
import vellum.storage.StorageExceptionType;
import vellum.logr.Logr;
import vellum.logr.LogrFactory;
import bizstat.server.BizstatServer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import vellum.config.ConfigMap;
import vellum.config.ConfigEntry;
import vellum.config.ConfigMapInitialisable;
import vellum.storage.StorageRuntimeException;

/**
 *
 * @author evan.summers
 */
public class BizstatConfigStorage implements Storage, ConfigMapInitialisable {
    static BizstatStorageMeta meta = new BizstatStorageMeta();
    
    Logr logger = LogrFactory.getLogger(BizstatConfigStorage.class);
    Map<Class, EntityMap> entityTypeMap = new HashMap();
    transient BizstatServer server; 
    
    public BizstatConfigStorage(BizstatServer server) {
        this.server = server;
    }

    public EntityMap getMap(Class type) {
        EntityMap map = entityTypeMap.get(type);
        if (map == null) {
            map = new EntityMap();
            entityTypeMap.put(type, map);
        }
        return map;
    }

    public void put(IdEntity entity) {
        getMap(entity.getClass()).put(entity);
    }
    
    public <E> List<E> getExtentList(Class<E> entityType) {
        return getMap(entityType).getExtentList();
    }

    public <E> List<E> getExtentList(Class<E> entityType, Matcher<E> matcher) {
        return getMap(entityType).getList(entityType, matcher);
    }
    
    @Override
    public <E> E get(Class<E> entityType, Comparable id) {
        return (E) getMap(entityType).get(id);
    }

    @Override
    public <E> E findNullable(Class<E> entityType, Comparable id) {
        if (id == null) return null;
        return find(entityType, id);
    }
    
    @Override
    public <E> E find(Class<E> entityType, Comparable id) {
        if (id == null) {    
            throw new StorageRuntimeException(StorageExceptionType.NULL_ID);
        }
        E entity = (E) getMap(entityType).get(id);
        if (entity == null) {
            throw new StorageRuntimeException(StorageExceptionType.NOT_FOUND, id);
        }
        return entity;
    }
    
    @Override
    public void init(ConfigMap configDocument) throws Exception {
        logger.info("init size", configDocument.getEntryList().size());
        Map<ConfigEntry, ConfigurableEntity> map = new HashMap();
        for (ConfigEntry section : configDocument.getEntryList()) {
            Class type = meta.getTypeMap().get(section.getType());
            if (type != null) {
                logger.trace("init", type);
                ConfigurableEntity configEntity = (ConfigurableEntity) type.newInstance();
                configEntity.setName(section.getName());
                put(configEntity);
                map.put(section, configEntity);
            }
        }
        for (ConfigEntry section : map.keySet()) { 
                ConfigurableEntity configEntity = map.get(section);
                configEntity.config(server, section.getProperties());
        }
    }
    
}
