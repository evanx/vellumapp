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
import venigma.entity.EntityPair;
import venigma.entity.IdPair;

/**
 *
 * @author evan
 */
public class PairMap<T extends EntityPair> {
    Logr logger = LogrFactory.getLogger(PairMap.class);
    List<T> list = new ArrayList();
    Map<IdPair, T> map = new HashMap();
    
    public PairMap() {
    }
    
    public boolean exists(Comparable id, Comparable otherId) {
        return map.containsKey(new IdPair(id, otherId));
    }

    public List<T> getList() {
        return list;
    }

    public Map<IdPair, T> getMap() {
        return map;
    }
    
    public T get(IdPair idPair) {
        if (idPair == null) {
            throw VStorageExceptionType.ID_NULL.newRuntimeException();    
        }
        return map.get(idPair);
    }

    public T find(IdPair idPair) throws VStorageException {
        if (idPair == null) {
            throw VStorageExceptionType.ID_NULL.newRuntimeException();    
        }
        if (!map.containsKey(idPair)) {
            throw VStorageExceptionType.PAIR_NOT_FOUND.newException();
        }
        return map.get(idPair);
    }

    public void add(T entityPair) throws VStorageException {
        if (entityPair == null) {
            throw VStorageExceptionType.ENTITY_NULL.newException();    
        }
        logger.info("add", entityPair);
        if (map.containsKey(entityPair.getIdPair())) {
            throw VStorageExceptionType.PAIR_ALREADY_EXISTS.newException();
        }
        map.put(entityPair.getIdPair(), entityPair);
    }
    
    public void update(T entityPair) throws VStorageException {
        if (entityPair == null) {
            throw VStorageExceptionType.ENTITY_NULL.newException();    
        }
        logger.info("update", entityPair);
        if (!map.containsKey(entityPair.getIdPair())) {
            throw VStorageExceptionType.PAIR_NOT_FOUND.newException();
        }
    }

    public void remove(T entityPair) throws VStorageException {
        if (entityPair == null) {
            throw VStorageExceptionType.ENTITY_NULL.newException();    
        }
        logger.info("remove", entityPair);
        if (!map.containsKey(entityPair.getIdPair())) {
            throw VStorageExceptionType.PAIR_NOT_FOUND.newException();
        }
    }
}
