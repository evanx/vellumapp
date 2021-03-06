/*
 Source https://code.google.com/p/vellum by @evanxsummers

 Licensed to the Apache Software Foundation (ASF) under one
 or more contributor license agreements. See the NOTICE file
 distributed with this work for additional information
 regarding copyright ownership. The ASF licenses this file to
 you under the Apache License, Version 2.0 (the "License").
 You may not use this file except in compliance with the
 License. You may obtain a copy of the License at:

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing,
 software distributed under the License is distributed on an
 "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 KIND, either express or implied.  See the License for the
 specific language governing permissions and limitations
 under the License.  
 */
package vellum.storage;

import vellum.entity.ComparableEntity;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.storage.StorageException;
import vellum.storage.StorageExceptionType;

/**
 *
 * @author evan.summers
 */
public abstract class AbstractMapEntityService<E extends ComparableEntity> implements EntityService<E> {

    private static final Logger logger = LoggerFactory.getLogger(AbstractMapEntityService.class);
    protected final Map<Comparable, E> keyMap = new TreeMap();
    private long idSequence = 1;
    
    @Override
    public void persist(E entity) throws StorageException {
        logger.info("insert {} {}", entity.getId(), !keyMap.containsKey(entity.getId()));
        if (entity instanceof AutoIdEntity) {
            AutoIdEntity idEntity = (AutoIdEntity) entity;
            idEntity.setId(idSequence++);
        }
        if (keyMap.put(entity.getId(), entity) != null) {
            throw new StorageException(StorageExceptionType.ALREADY_EXISTS, entity.getId());
        }
    }

    @Override
    public void update(E entity) throws StorageException {
        if (keyMap.put(entity.getId(), entity) == null) {
            throw new StorageException(StorageExceptionType.NOT_FOUND, entity.getId());
        }
    }

    @Override
    public boolean retrievable(Comparable key) throws StorageException {
        logger.debug("containsKey {}", key, keyMap.containsKey(key));
        return keyMap.containsKey(key);
    }
    
    @Override
    public void remove(Comparable key) throws StorageException {
        logger.info("delete {} {}", key, keyMap.containsKey(key));
        if (keyMap.remove(key) != null) {
            throw new StorageException(StorageExceptionType.NOT_FOUND, key);           
        }
    }

    @Override
    public E find(Comparable key) throws StorageException {
        logger.info("select {} {}", key, keyMap.containsKey(key));
        return keyMap.get(key);
    }

    @Override
    public E retrieve(Comparable key) throws StorageException {
        E entity = find(key);
        if (entity == null) {
            throw new StorageException(StorageExceptionType.NOT_FOUND, key);           
        }
        return entity;
    }

    @Override
    public Collection<E> list() throws StorageException {
        return keyMap.values();
    }    
}
