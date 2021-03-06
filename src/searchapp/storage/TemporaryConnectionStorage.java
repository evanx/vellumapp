/*
 * Source https://github.com/evanx by @evanxsummers

       Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements. See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership.  The ASF licenses this file
       to you under the Apache License, Version 2.0 (the
       "License"); you may not use this file except in compliance
       with the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.  
 */
package searchapp.storage;

import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import searchapp.entity.ConnectionEntity;
import vellum.storage.EntityMatcher;
import vellum.storage.MapEntityService;
import vellum.storage.StorageException;

/**
 * 
 * @author evan.summers
 */
public class TemporaryConnectionStorage extends MapEntityService<ConnectionEntity> 
        implements ConnectionStorage {
    static Logger logger = LoggerFactory.getLogger(TemporaryConnectionStorage.class);

    public TemporaryConnectionStorage(EntityMatcher matcher) {
        super(matcher);
    }

    @Override
    public Collection<ConnectionEntity> list(Comparable key) throws StorageException {
        return list();
    }
    
}
