/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package crocserver.storage.adminuser;

import crocserver.storage.common.CrocStorage;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import vellumexp.logr.Logr;
import vellumexp.logr.LogrFactory;
import vellum.sql.QueryMap;
import vellum.query.RowSets;
import vellum.connection.ConnectionEntry;
import vellum.storage.StorageException;
import vellum.storage.StorageExceptionType;
import vellum.util.Lists;

/**
 *
 * @author evan.summers
 */
public class AdminUserStorage {

    static Logr logger = LogrFactory.getLogger(AdminUserStorage.class);
    static QueryMap sqlMap = new QueryMap(AdminUserStorage.class);
    CrocStorage storage;

    public AdminUserStorage(CrocStorage storage) {
        this.storage = storage;
    }

    public void store(AdminUser user) throws SQLException {
        if (user.isStored()) {
            update(user);
        } else {
            insert(user);
        }
    }
    
    public void validate() throws SQLException {
        ConnectionEntry connection = storage.getConnectionPool().takeEntry();
        try {
            PreparedStatement statement = connection.prepareStatement(
                    sqlMap.get(AdminUserQuery.validate.name()));
            ResultSet resultSet = statement.executeQuery();
            List<String> columnNameList = RowSets.getColumnNameList(resultSet.getMetaData());
            for (Enum columnNameEnum : AdminUserMeta.values()) {
                String columnName = columnNameEnum.name().toUpperCase();
                logger.info("validate", columnName);
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
    
    private AdminUser get(ResultSet resultSet) throws SQLException {
        AdminUser user = new AdminUser();
        user.setId(resultSet.getLong(AdminUserMeta.user_id.name()));
        user.setUserName(resultSet.getString(AdminUserMeta.user_name.name()));
        user.setFirstName(resultSet.getString(AdminUserMeta.first_name.name()));
        user.setLastName(resultSet.getString(AdminUserMeta.last_name.name()));
        user.setDisplayName(resultSet.getString(AdminUserMeta.display_name.name()));
        user.setEmail(resultSet.getString(AdminUserMeta.email.name()));
        user.setSubject(resultSet.getString(AdminUserMeta.subject.name()));
        user.setCert(resultSet.getString(AdminUserMeta.cert.name()));
        user.setSecret(resultSet.getString(AdminUserMeta.secret.name()));
        user.setLoginTime(resultSet.getTimestamp(AdminUserMeta.login.name()));
        user.setLogoutTime(resultSet.getTimestamp(AdminUserMeta.logout.name()));
        user.setUpdated(resultSet.getTimestamp(AdminUserMeta.updated.name()));
        user.setStored(true);
        return user;
    }
    
    public void insert(AdminUser user) throws SQLException, StorageException {
        ConnectionEntry connection = storage.getConnectionPool().takeEntry();
        try {
            PreparedStatement statement = connection.prepareStatement(
                sqlMap.get(AdminUserQuery.insert.name()));
            int index = 0;
            statement.setString(++index, user.getUserName());
            statement.setString(++index, user.getFirstName());
            statement.setString(++index, user.getLastName());
            statement.setString(++index, user.getDisplayName());
            statement.setString(++index, user.getEmail());
            statement.setString(++index, user.getSubject());
            statement.setString(++index, user.getSecret());
            statement.setTimestamp(++index, new Timestamp(user.getLoginTime().getTime()));
            int updateCount = statement.executeUpdate();
            connection.setOk(true);
            if (updateCount != 1) {
                throw new StorageException(StorageExceptionType.NOT_FOUND);
            }
        } finally {
            storage.getConnectionPool().releaseConnection(connection);
        }
    }

    public boolean exists(String userName) throws SQLException {
        ConnectionEntry connection = storage.getConnectionPool().takeEntry();
        try {
            PreparedStatement statement = connection.prepareStatement(sqlMap.get(AdminUserQuery.exists_username.name()));
            statement.setString(1, userName);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            int count = resultSet.getInt(1);
            connection.setOk(true);
            return count == 1;
        } finally {
            storage.getConnectionPool().releaseConnection(connection);
        }
    }

    public boolean existsEmail(String email) throws SQLException {
        ConnectionEntry connection = storage.getConnectionPool().takeEntry();
        try {
            PreparedStatement statement = connection.prepareStatement(sqlMap.get(AdminUserQuery.exists_email.name()));
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            int count = resultSet.getInt(1);
            connection.setOk(true);
            return count > 0;
        } finally {
            storage.getConnectionPool().releaseConnection(connection);
        }
    }
    
    public AdminUser get(String username) throws SQLException, StorageException {
        AdminUser adminUser = find(username);
        if (adminUser == null) {
            throw new StorageException(StorageExceptionType.NOT_FOUND, username);
        }
        return adminUser;
    }

    public AdminUser getEmail(String username) throws SQLException, StorageException {
        AdminUser adminUser = findEmail(username);
        if (adminUser == null) {
            throw new StorageException(StorageExceptionType.NOT_FOUND, username);
        }
        return adminUser;
    }
    
    public AdminUser find(String username) throws SQLException {
        ConnectionEntry connection = storage.getConnectionPool().takeEntry();
        try {
            PreparedStatement statement = connection.prepareStatement(
                    sqlMap.get(AdminUserQuery.find_username.name()));
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.next()) {
                return null;
            }
            return get(resultSet);
        } finally {
            storage.getConnectionPool().releaseConnection(connection);
        }
    }
    
    public AdminUser findEmail(String email) throws SQLException {
        ConnectionEntry connection = storage.getConnectionPool().takeEntry();
        try {
            PreparedStatement statement = connection.prepareStatement(
                    sqlMap.get(AdminUserQuery.find_email.name()));
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.next()) {
                return null;
            }
            return get(resultSet);
        } finally {
            storage.getConnectionPool().releaseConnection(connection);
        }
    }

    public void update(AdminUser user) throws SQLException {
        ConnectionEntry connection = storage.getConnectionPool().takeEntry();
        try {
            PreparedStatement statement = connection.prepareStatement(
                    sqlMap.get(AdminUserQuery.update_display_name.name()));
            int index = 0;
            statement.setString(++index, user.getDisplayName());
            statement.setString(++index, user.getUserName());
            int updateCount = statement.executeUpdate();
            connection.setOk(true);
            if (updateCount != 1) {
                throw new SQLException();
            }
        } finally {
            storage.getConnectionPool().releaseConnection(connection);
        }
    }

    public void updateSecret(AdminUser user) throws SQLException {
        ConnectionEntry connection = storage.getConnectionPool().takeEntry();
        try {
            PreparedStatement statement = connection.prepareStatement(
                    sqlMap.get(AdminUserQuery.update_secret.name()));
            statement.setString(1, user.getSecret());
            statement.setString(2, user.getUserName());
            int updateCount = statement.executeUpdate();
            connection.setOk(true);
            if (updateCount != 1) {
                throw new SQLException();
            }
        } finally {
            storage.getConnectionPool().releaseConnection(connection);
        }
    }

    public void updateCert(AdminUser user) throws SQLException {
        ConnectionEntry connection = storage.getConnectionPool().takeEntry();
        try {
            PreparedStatement statement = connection.prepareStatement(
                    sqlMap.get(AdminUserQuery.update_cert.name()));
            statement.setString(1, user.getSubject());
            statement.setString(2, user.getCert());
            statement.setString(3, user.getUserName());
            int updateCount = statement.executeUpdate();
            connection.setOk(true);
            if (updateCount != 1) {
                throw new SQLException();
            }
        } finally {
            storage.getConnectionPool().releaseConnection(connection);
        }
    }
    
    public void updateLogin(AdminUser user) throws SQLException {
        ConnectionEntry connection = storage.getConnectionPool().takeEntry();
        try {
            PreparedStatement statement = connection.prepareStatement(
                    sqlMap.get(AdminUserQuery.update_login.name()));
            statement.setTimestamp(1, new Timestamp(user.getLoginTime().getTime()));
            statement.setString(2, user.getUserName());
            int updateCount = statement.executeUpdate();
            connection.setOk(true);
            if (updateCount != 1) {
                throw new SQLException();
            }
        } finally {
            storage.getConnectionPool().releaseConnection(connection);
        }
    }

    public void updateLogout(AdminUser user) throws SQLException {
        ConnectionEntry connection = storage.getConnectionPool().takeEntry();
        try {
            PreparedStatement statement = connection.prepareStatement(
                    sqlMap.get(AdminUserQuery.update_logout.name()));
            statement.setTimestamp(1, new Timestamp(user.getLoginTime().getTime()));
            statement.setString(2, user.getUserName());
            int updateCount = statement.executeUpdate();
            connection.setOk(true);
            if (updateCount != 1) {
                throw new SQLException();
            }
        } finally {
            storage.getConnectionPool().releaseConnection(connection);
        }
    }
    
    public List<AdminUser> getList() throws SQLException {
        ConnectionEntry connection = storage.getConnectionPool().takeEntry();
        try {
            List<AdminUser> list = new ArrayList();
            PreparedStatement statement = connection.prepareStatement(sqlMap.get(AdminUserQuery.list.name()));
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
