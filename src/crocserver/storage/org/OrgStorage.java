/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package crocserver.storage.org;

import crocserver.storage.adminuser.AdminUser;
import crocserver.storage.common.CrocStorage;
import vellum.entity.AbstractEntityStorage;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import vellum.query.QueryMap;
import vellum.storage.ConnectionEntry;
import vellum.storage.StorageException;
import vellum.storage.StorageExceptionType;

/**
 *
 * @author evan.summers
 */
public class OrgStorage extends AbstractEntityStorage<Long, Org> {

    static QueryMap sqlMap = new QueryMap(OrgStorage.class);
    CrocStorage storage;

    public OrgStorage(CrocStorage storage) {
        this.storage = storage;
    }

    private Org get(ResultSet resultSet) throws SQLException {
        Org org = new Org();
        org.setId(resultSet.getLong(OrgMeta.org_id.name()));
        org.setOrgName(resultSet.getString(OrgMeta.org_name.name()));
        org.setDisplayName(resultSet.getString(OrgMeta.display_name.name()));
        org.setUrl(resultSet.getString(OrgMeta.url.name()));
        org.setUpdated(resultSet.getTimestamp(OrgMeta.updated.name()));
        org.setStored(true);
        return org;
    }

    public Long insert(Org org) throws SQLException {
        logger.info("insert", org);
        ConnectionEntry connection = storage.getConnectionPool().takeEntry();
        try {
            PreparedStatement statement = connection.prepareStatement(
                sqlMap.get(OrgQuery.insert.name()));
            int index = 0;
            statement.setString(++index, org.getOrgName());
            statement.setString(++index, org.getUrl());
            statement.setString(++index, org.getDisplayName());
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
            org.setId(id);
            org.setStored(true);
            connection.setOk(true);
            return id;
        } finally {
            storage.getConnectionPool().releaseConnection(connection);
        }
    }

    public boolean exists(String email) throws SQLException {
        ConnectionEntry connection = storage.getConnectionPool().takeEntry();
        try {
            PreparedStatement statement = connection.prepareStatement(sqlMap.get(OrgQuery.exists.name()));
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();
            boolean exists = resultSet.next();
            connection.setOk(true);
            return exists;
        } finally {
            storage.getConnectionPool().releaseConnection(connection);
        }
    }

    public Org get(String name) throws SQLException {
        Org org = find(name);
        if (org == null) {
            throw new StorageException(StorageExceptionType.NOT_FOUND, name);
        }
        return org;
    }

    public Org get(long id) throws SQLException {
        Org org = find(id);
        if (org == null) {
            throw new StorageException(StorageExceptionType.NOT_FOUND, id);
        }
        return org;
    }
        
    public Org find(String name) throws SQLException {
        logger.info("find", name);
        ConnectionEntry connection = storage.getConnectionPool().takeEntry();
        try {
            PreparedStatement statement = connection.prepareStatement(
                    sqlMap.get(OrgQuery.find_name.name()));
            statement.setString(1, name);
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
    public Org find(Long id) throws SQLException {
        ConnectionEntry connection = storage.getConnectionPool().takeEntry();
        try {
            PreparedStatement statement = connection.prepareStatement(
                    sqlMap.get(OrgQuery.find_id.name()));
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

    public void update(Org org) throws SQLException {
        ConnectionEntry connection = storage.getConnectionPool().takeEntry();
        try {
            PreparedStatement statement = connection.prepareStatement(
                    sqlMap.get(OrgQuery.update.name()));
            int index = 0;
            statement.setString(++index, org.getUrl());
            statement.setString(++index, org.getDisplayName());
            statement.setLong(++index, org.getId());
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

    public List<Org> getList() throws SQLException {
        ConnectionEntry connection = storage.getConnectionPool().takeEntry();
        try {
            List<Org> list = new ArrayList();
            PreparedStatement statement = connection.prepareStatement(sqlMap.get(OrgQuery.list.name()));
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
