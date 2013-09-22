/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package venigma.server.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import vellum.logr.Logr;
import vellum.logr.LogrFactory;
import venigma.entity.IdEntity;

/**
 *
 * @author evan
 */
public class IdEntityMap<T extends IdEntity> {
    Logr logger = LogrFactory.getLogger(IdEntityMap.class);
    List<T> list = new ArrayList();
    Map<Comparable, T> map = new HashMap();
    
    public IdEntityMap() {
    }
    
    public void init(List<T> entityList) {
        this.list.addAll(entityList);
        for (T entity : entityList) {
            map.put(entity.getId(), entity);
        }
    }

    public boolean exists(Comparable id) {
        if (id == null) {
            throw new VStorageRuntimeException(VStorageExceptionType.ID_NULL);
        }
        return map.containsKey(id);
    }

    public List<T> getList() {
        return list;
    }

    public Map<Comparable, T> getMap() {
        return map;
    }
    
    public T get(Comparable id) {
        if (id == null) {
            throw VStorageExceptionType.ID_NULL.newRuntimeException();
        }
        return map.get(id);
    }

    public T find(Comparable id) throws VStorageException {
        if (id == null) {
            throw VStorageExceptionType.ID_NULL.newException();
        }
        if (!map.containsKey(id)) {
            throw VStorageExceptionType.ID_NOT_FOUND.newException();
        }
        return map.get(id);
    }

    public void add(T entity) throws VStorageException {
        logger.info("add", entity);
        if (entity == null) {
            throw VStorageExceptionType.ENTITY_NULL.newException();
        }
        if (map.containsKey(entity.getId())) {
            throw VStorageExceptionType.ID_ALREADY_EXISTS.newException();
        }
        map.put(entity.getId(), entity);
    }
    
    public void update(T entity) throws VStorageException {
        logger.info("update", entity);
        if (entity == null) {
            throw VStorageExceptionType.ENTITY_NULL.newException();
        }
        if (!map.containsKey(entity.getId())) {
            throw VStorageExceptionType.ID_NOT_FOUND.newException();
        }
    }

    public void remove(T entity) throws VStorageException {
        logger.info("remove", entity);
        if (entity == null) {
            throw VStorageExceptionType.ENTITY_NULL.newException();
        }
        if (!map.containsKey(entity.getId())) {
            throw VStorageExceptionType.ID_NOT_FOUND.newException();
        }
    }
}
