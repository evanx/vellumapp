/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package crocserver.storage.service;

import crocserver.storage.common.CrocStorage;
import java.sql.*;
import vellum.storage.StorageExceptionType;
import java.util.ArrayList;
import java.util.List;
import vellum.query.QueryMap;
import vellum.logr.Logr;
import vellum.logr.LogrFactory;
import vellum.connection.ConnectionEntry;
import vellum.storage.StorageException;

/**
 *
 * @author evan.summers
 */
public class ServiceStorage {

    static QueryMap sqlMap = new QueryMap(ServiceStorage.class);
    Logr logger = LogrFactory.getLogger(ServiceStorage.class);
    CrocStorage storage;

    public ServiceStorage(CrocStorage storage) {
        this.storage = storage;
    }

    private Service build(ResultSet resultSet) throws SQLException {
        Service cert = new Service();
        cert.setId(resultSet.getLong(ServiceMeta.client_cert_id.name()));
        cert.setOrgId(resultSet.getLong(ServiceMeta.org_id.name()));
        cert.setHostName(resultSet.getString(ServiceMeta.host_name.name()));
        cert.setServiceName(resultSet.getString(ServiceMeta.account_name.name()));
        cert.setSubject(resultSet.getString(ServiceMeta.subject.name()));
        cert.setCert(resultSet.getString(ServiceMeta.cert.name()));
        cert.setUpdated(resultSet.getTimestamp(ServiceMeta.updated.name()));
        cert.setUpdatedBy(resultSet.getString(ServiceMeta.updated_by.name()));
        cert.setStored(true);
        return cert;
    }

    public void insert(Service cert) throws StorageException, SQLException {
        ConnectionEntry connection = storage.getConnectionPool().takeEntry();
        try {
            PreparedStatement statement = connection.prepareStatement(sqlMap.get(ServiceQuery.insert.name()));
            int index = 0;
            statement.setLong(++index, cert.getOrgId());
            statement.setString(++index, cert.getHostName());
            statement.setString(++index, cert.getServiceName());
            statement.setString(++index, cert.getSubject());
            statement.setString(++index, cert.getCert());
            statement.setString(++index, cert.getUpdatedBy());
            int insertCount = statement.executeUpdate();
            if (insertCount != 1) {
                throw new StorageException(StorageExceptionType.NOT_INSERTED);
            }
            ResultSet generatedKeys = statement.getGeneratedKeys();
            generatedKeys.next();
            cert.setId(generatedKeys.getLong((1)));
            cert.setStored(true);
            connection.setOk(true);
        } finally {
            storage.getConnectionPool().releaseConnection(connection);
        }
    }

    public void updateCert(Service cert) throws SQLException {
        ConnectionEntry connection = storage.getConnectionPool().takeEntry();
        try {
            PreparedStatement statement = connection.prepareStatement(
                    sqlMap.get(ServiceQuery.update_cert.name()));
            int index = 0;
            statement.setString(++index, cert.getSubject());
            statement.setString(++index, cert.getCert());
            statement.setString(++index, cert.getUpdatedBy());
            statement.setLong(++index, cert.getId());
            int updateCount = statement.executeUpdate();
            if (updateCount != 1) {
                throw new StorageException(StorageExceptionType.UPDATE_COUNT);
            }
        } finally {
            storage.getConnectionPool().releaseConnection(connection);
        }
    }

    public Service findSubject(String subject) throws SQLException {
        ConnectionEntry connection = storage.getConnectionPool().takeEntry();
        try {
            PreparedStatement statement = connection.prepareStatement(sqlMap.get(ServiceQuery.find_subject.name()));
            statement.setString(1, subject);
            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.next()) {
                return null;
            }
            return build(resultSet);
        } finally {
            storage.getConnectionPool().releaseConnection(connection);
        }
    }

    public Service find(long id) throws SQLException {
        ConnectionEntry connection = storage.getConnectionPool().takeEntry();
        try {
            PreparedStatement statement = connection.prepareStatement(sqlMap.get(ServiceQuery.find_id.name()));
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.next()) {
                return null;
            }
            return build(resultSet);
        } finally {
            storage.getConnectionPool().releaseConnection(connection);
        }
    }

    public Service get(long orgId, String hostName, String clientName) throws SQLException {
        Service clientCert = find(orgId, hostName, clientName);
        if (clientCert == null) {
            throw new StorageException(StorageExceptionType.NOT_FOUND, orgId, hostName, clientName);
        }
        return clientCert;
    }
    
    public Service find(long orgId, String hostName, String accountName) throws SQLException {
        ConnectionEntry connection = storage.getConnectionPool().takeEntry();
        try {
            PreparedStatement statement = connection.prepareStatement(
                    sqlMap.get(ServiceQuery.find_org_host_account.name()));
            statement.setLong(1, orgId);
            statement.setString(2, hostName);
            statement.setString(3, accountName);
            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.next()) {
                return null;
            }
            return build(resultSet);
        } finally {
            storage.getConnectionPool().releaseConnection(connection);
        }
    }

    public List<Service> getList() throws SQLException {
        ConnectionEntry connection = storage.getConnectionPool().takeEntry();
        try {
            List<Service> list = new ArrayList();
            PreparedStatement statement = connection.prepareStatement(
                    sqlMap.get(ServiceQuery.list.name()));
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                list.add(build(resultSet));
            }
            connection.setOk(true);
            return list;
        } finally {
            storage.getConnectionPool().releaseConnection(connection);
        }
    }

    public List<Service> getList(long orgId) throws SQLException {
        ConnectionEntry connection = storage.getConnectionPool().takeEntry();
        try {
            List<Service> list = new ArrayList();
            PreparedStatement statement = connection.prepareStatement(
                    sqlMap.get(ServiceQuery.list_org.name()));
            statement.setLong(1, orgId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                list.add(build(resultSet));
            }
            connection.setOk(true);
            return list;
        } finally {
            storage.getConnectionPool().releaseConnection(connection);
        }
    }
}
