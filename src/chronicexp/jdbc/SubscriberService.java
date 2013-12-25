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
package chronicexp.jdbc;

import chronic.entity.Subscription;
import chronic.entitykey.SubscriberKey;
import chronic.entitykey.PersonKey;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.sql.QueryMap;
import vellum.storage.EntityService;
import vellum.storage.StorageException;
import vellum.storage.StorageExceptionType;

/**
 *
 * @author evan.summers
 */
public class SubscriberService implements EntityService<Subscription> {

    static Logger logger = LoggerFactory.getLogger(SubscriberService.class);
    static QueryMap queryMap = new QueryMap(SubscriberService.class);
    Connection connection;

    public SubscriberService(Connection connection) {
        this.connection = connection;
    }

    private PreparedStatement prepare(String queryKey,
            Object... parameters) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(queryMap.get(queryKey));
        int index = 0;
        for (Object parameter : parameters) {
            statement.setObject(++index, parameter);
        }
        return statement;
    }

    private Subscription create(ResultSet resultSet) throws SQLException {
        Subscription subscriber = new Subscription();
        subscriber.setId(resultSet.getLong("topic_sub_id"));
        subscriber.setTopicId(resultSet.getLong("topic_id"));
        subscriber.setEmail(resultSet.getString("email"));
        subscriber.setEnabled(resultSet.getBoolean("enabled"));
        return subscriber;
    }

    private Collection<Subscription> list(ResultSet resultSet) throws SQLException {
        Collection list = new LinkedList();
        while (resultSet.next()) {
            list.add(create(resultSet));
        }
        return list;
    }
    
    @Override
    public void persist(Subscription subscriber) throws StorageException {
        try (PreparedStatement statement = prepare("insert", subscriber.getTopicId(),
                subscriber.getEmail(), subscriber.isEnabled())) {
            ResultSet generatedKeys = statement.executeQuery();
            if (!generatedKeys.next()) {
                throw new StorageException(StorageExceptionType.NOT_PERSISTED);
            }
            subscriber.setId(generatedKeys.getLong(1));
        } catch (SQLException sqle) {
            throw new StorageException(sqle, StorageExceptionType.SQL, subscriber.getId());
        }
    }

    @Override
    public void update(Subscription subscriber) throws StorageException {
        updateEnabled(subscriber);
    }

    public void updateEnabled(Subscription subscriber) throws StorageException {
        try (PreparedStatement statement = prepare("update enabled", 
                subscriber.isEnabled(), subscriber.getId())) {
            if (statement.executeUpdate() != 1) {
                throw new StorageException(StorageExceptionType.NOT_UPDATED, subscriber.getId());
            }
        } catch (SQLException sqle) {
            throw new StorageException(sqle, StorageExceptionType.SQL, subscriber.getId());
        }
    }

    @Override
    public void remove(Comparable key) throws StorageException {
        try (PreparedStatement statement = prepare("delete", key)) {
            if (statement.executeUpdate() != 1) {
                throw new StorageException(StorageExceptionType.NOT_DELETED, key);
            }
        } catch (SQLException sqle) {
            throw new StorageException(sqle, StorageExceptionType.SQL, key);
        }
    }

    @Override
    public Subscription find(Comparable key) throws StorageException {
        if (key instanceof Long) {
            return findId((Long) key);
        } else if (key instanceof SubscriberKey) {
            return findKey((SubscriberKey) key);
        }
        throw new StorageException(StorageExceptionType.INVALID_KEY, 
                key.getClass().getSimpleName());
    }

    private Subscription findKey(SubscriberKey key) throws StorageException {
        try (PreparedStatement statement = prepare("select key",
                key.getTopicId(), key.getEmail());
                ResultSet resultSet = statement.executeQuery()) {
            if (!resultSet.next()) {
                return null;
            }
            Subscription subscriber = create(resultSet);
            if (resultSet.next()) {
                throw new StorageException(StorageExceptionType.MULTIPLE_FOUND, key);
            }
            return subscriber;
        } catch (SQLException sqle) {
            throw new StorageException(sqle, StorageExceptionType.SQL, 
                    key);
        }
    }
    private Subscription findId(Long id) throws StorageException {
        try (PreparedStatement statement = prepare("select id", id);
                ResultSet resultSet = statement.executeQuery()) {
            if (!resultSet.next()) {
                return null;
            }
            Subscription subscriber = create(resultSet);
            if (resultSet.next()) {
                throw new StorageException(StorageExceptionType.MULTIPLE_FOUND, id);
            }
            return subscriber;
        } catch (SQLException sqle) {
            throw new StorageException(sqle, StorageExceptionType.SQL, 
                    id);
        }
    }

    @Override
    public boolean retrievable(Comparable key) throws StorageException {
        return find(key) != null;
    }

    @Override
    public Subscription retrieve(Comparable key) throws StorageException {
        Subscription subscriber = find(key);
        if (subscriber == null) {
            throw new StorageException(StorageExceptionType.NOT_FOUND, key);
        }
        return subscriber;
    }

    @Override
    public Collection<Subscription> list() throws StorageException {
        try (PreparedStatement statement = prepare("list");
                ResultSet resultSet = statement.executeQuery()) {
            return list(resultSet);
        } catch (SQLException sqle) {
            throw new StorageException(sqle, StorageExceptionType.SQL);
        }
    }

    @Override
    public Collection<Subscription> list(Comparable key) throws StorageException {
        if (key instanceof String) {
            return listPerson((String) key);
        } else if (key instanceof Long) {
            return listTopic((Long) key);
        } else if (key instanceof PersonKey) {
            return listPerson(((PersonKey) key).getEmail());
        }
        throw new StorageException(StorageExceptionType.INVALID_KEY, key.getClass().getSimpleName());
    }

    private Collection<Subscription> listTopic(Long topicId) throws StorageException {
        try (PreparedStatement statement = prepare("list topic", topicId);
                ResultSet resultSet = statement.executeQuery()) {
            return list(resultSet);
        } catch (SQLException sqle) {
            throw new StorageException(sqle, StorageExceptionType.SQL);
        }
    }
    
    private Collection<Subscription> listPerson(String email) throws StorageException {
        try (PreparedStatement statement = prepare("list email", email);
                ResultSet resultSet = statement.executeQuery()) {
            return list(resultSet);
        } catch (SQLException sqle) {
            throw new StorageException(sqle, StorageExceptionType.SQL);
        }
    }

}
