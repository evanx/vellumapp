/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package saltserver.storage.secret;

import vellum.entity.AbstractEntityStorage;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import saltserver.app.VaultStorage;
import vellum.query.QueryMap;
import vellum.query.RowSets;
import vellum.storage.ConnectionEntry;
import vellum.storage.StorageException;
import vellum.storage.StorageExceptionType;
import vellum.util.Lists;

/**
 *
 * @author evan.summers
 */
public class SecretStorage extends AbstractEntityStorage<Long, Secret> {

    static QueryMap sqlMap = new QueryMap(SecretStorage.class);
    VaultStorage storage;

    public SecretStorage(VaultStorage storage) {
        this.storage = storage;
    }

    public void validate() throws SQLException {
        ConnectionEntry connection = storage.getConnectionPool().takeEntry();
        try {
            PreparedStatement statement = connection.prepareStatement(
                    sqlMap.get(SecretQuery.validate.name()));
            ResultSet resultSet = statement.executeQuery();
            List<String> columnNameList = RowSets.getColumnNameList(resultSet.getMetaData());
            for (Enum columnNameEnum : SecretMeta.values()) {
                String columnName = columnNameEnum.name().toUpperCase();
                if (!columnNameList.contains(columnName)) {
                    throw new SQLException(columnName);
                }            
                columnNameList.remove(columnName);
            }
            if (!columnNameList.isEmpty()) {
                throw new SQLException(Lists.format(columnNameList));
            }
        } finally {
            storage.getConnectionPool().releaseConnection(connection);
        }
    }
    
    private Secret get(ResultSet resultSet) throws SQLException {
        Secret secret = new Secret();
        secret.setId(resultSet.getLong(SecretMeta.secret_id.name()));
        secret.setGroup(resultSet.getString(SecretMeta.group_.name()));
        secret.setName(resultSet.getString(SecretMeta.name_.name()));
        secret.setSecret(resultSet.getString(SecretMeta.secret.name()));
        secret.setStored(true);
        return secret;
    }

    public Long insert(Secret secret) throws SQLException {
        ConnectionEntry connection = storage.getConnectionPool().takeEntry();
        try {
            PreparedStatement statement = connection.prepareStatement(
                sqlMap.get(SecretQuery.insert.name()));
            int index = 0;
            statement.setString(++index, secret.getGroup());
            statement.setString(++index, secret.getName());
            statement.setString(++index, secret.getSecret());
            int updateCount = statement.executeUpdate();
            connection.setOk(true);
            if (updateCount != 1) {
                throw new SQLException();
            }
            ResultSet resultSet = statement.getGeneratedKeys();
            if (!resultSet.next()) {
                throw new SQLException();    
            }
            long id = resultSet.getLong(1);
            secret.setId(id);
            secret.setStored(true);
            connection.setOk(true);
            return id;
        } finally {
            storage.getConnectionPool().releaseConnection(connection);
        }
    }

    public boolean exists(String group, String name) throws SQLException {
        ConnectionEntry connection = storage.getConnectionPool().takeEntry();
        try {
            PreparedStatement statement = connection.prepareStatement(sqlMap.get(SecretQuery.exists.name()));
            int index = 0;
            statement.setString(++index, group);
            statement.setString(++index, name);
            ResultSet resultSet = statement.executeQuery();
            boolean exists = resultSet.next();
            connection.setOk(true);
            return exists;
        } finally {
            storage.getConnectionPool().releaseConnection(connection);
        }
    }

    public Secret get(String group, String name) throws SQLException {
        Secret secret = find(group, name);
        if (secret == null) {
            throw new StorageException(StorageExceptionType.NOT_FOUND, group, name);
        }
        return secret;
    }
    
    public Secret find(String group, String name) throws SQLException {
        ConnectionEntry connection = storage.getConnectionPool().takeEntry();
        try {
            PreparedStatement statement = connection.prepareStatement(
                    sqlMap.get(SecretQuery.find.name()));
            int index = 0;
            statement.setString(++index, group);
            statement.setString(++index, name);
            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.next()) {
                return null;
            }
            connection.setOk(true);
            return get(resultSet);
        } finally {
            storage.getConnectionPool().releaseConnection(connection);
        }
    }
    
    @Override
    public Secret find(Long id) throws SQLException {
        ConnectionEntry connection = storage.getConnectionPool().takeEntry();
        try {
            PreparedStatement statement = connection.prepareStatement(
                    sqlMap.get(SecretQuery.find_id.name()));
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.next()) {
                return null;
            }
            connection.setOk(true);
            return get(resultSet);
        } finally {
            storage.getConnectionPool().releaseConnection(connection);
        }
    }

    public void update(Secret secret) throws SQLException {
        ConnectionEntry connection = storage.getConnectionPool().takeEntry();
        try {
            PreparedStatement statement = connection.prepareStatement(
                    sqlMap.get(SecretQuery.update.name()));
            int index = 0;
            statement.setString(++index, secret.getSecret());
            statement.setLong(++index, secret.getId());
            int updateCount = statement.executeUpdate();
            connection.setOk(true);
            if (updateCount != 1) {
                throw new SQLException();
            }
            connection.setOk(true);
        } finally {
            storage.getConnectionPool().releaseConnection(connection);
        }
    }

    public List<Secret> getList() throws SQLException {
        ConnectionEntry connection = storage.getConnectionPool().takeEntry();
        try {
            List<Secret> list = new ArrayList();
            PreparedStatement statement = connection.prepareStatement(sqlMap.get(SecretQuery.list.name()));
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                list.add(get(resultSet));
            }
            connection.setOk(true);
            return list;
        } finally {
            storage.getConnectionPool().releaseConnection(connection);
        }
    }

}
