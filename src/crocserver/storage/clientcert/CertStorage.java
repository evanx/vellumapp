/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package crocserver.storage.clientcert;

import crocserver.storage.common.CrocStorage;
import java.security.cert.X509Certificate;
import java.sql.*;
import vellum.storage.StorageExceptionType;
import java.util.ArrayList;
import java.util.List;
import vellum.sql.QueryMap;
import vellum.logr.Logr;
import vellum.logr.LogrFactory;
import vellum.connection.ConnectionEntry;
import vellum.storage.StorageException;

/**
 *
 * @author evan.summers
 */
public class CertStorage {

    static QueryMap sqlMap = new QueryMap(CertStorage.class);
    Logr logger = LogrFactory.getLogger(CertStorage.class);
    CrocStorage storage;

    public CertStorage(CrocStorage storage) {
        this.storage = storage;
    }

    private Cert build(ResultSet resultSet) throws SQLException {
        Cert cert = new Cert();
        cert.setId(resultSet.getLong(CertMeta.cert_id.name()));
        cert.setOrgId(resultSet.getLong(CertMeta.org_id.name()));
        cert.setName(resultSet.getString(CertMeta.name_.name()));
        cert.setSubject(resultSet.getString(CertMeta.subject.name()));
        cert.setCert(resultSet.getString(CertMeta.cert.name()));
        cert.setUpdated(resultSet.getTimestamp(CertMeta.updated.name()));
        cert.setStored(true);
        return cert;
    }

    public void insert(Cert cert) throws StorageException, SQLException {
        ConnectionEntry connection = storage.getConnectionPool().takeEntry();
        try {
            PreparedStatement statement = connection.prepareStatement(sqlMap.get(CertQuery.insert.name()));
            int index = 0;
            statement.setLong(++index, cert.getOrgId());
            statement.setString(++index, cert.getName());
            statement.setString(++index, cert.getSubject());
            statement.setString(++index, cert.getCert());
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

    public void update(Cert cert) throws SQLException {
        ConnectionEntry connection = storage.getConnectionPool().takeEntry();
        try {
            PreparedStatement statement = connection.prepareStatement(
                    sqlMap.get(CertQuery.update.name()));
            int index = 0;
            statement.setString(++index, cert.getCert());
            statement.setString(++index, cert.getName());
            int updateCount = statement.executeUpdate();
            if (updateCount != 1) {
                throw new StorageException(StorageExceptionType.UPDATE_COUNT);
            }
        } finally {
            storage.getConnectionPool().releaseConnection(connection);
        }
    }

    public void save(Cert cert) throws SQLException {
        if (cert.isStored()) {
            update(cert);
        } else {
            insert(cert);
        }
    }
        
    public Cert find(long id) throws SQLException {
        ConnectionEntry connection = storage.getConnectionPool().takeEntry();
        try {
            PreparedStatement statement = connection.prepareStatement(sqlMap.get(CertQuery.find_id.name()));
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

    public Cert findName(String name) throws SQLException {
        ConnectionEntry connection = storage.getConnectionPool().takeEntry();
        try {
            String sqlQuery = sqlMap.get(CertQuery.find_name.name());
            PreparedStatement statement = connection.prepareStatement(sqlQuery);
            statement.setString(1, name);
            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.next()) {
                return null;
            }
            return build(resultSet);
        } finally {
            storage.getConnectionPool().releaseConnection(connection);
        }
    }
    
    public Cert findSubject(String subject) throws SQLException {
        ConnectionEntry connection = storage.getConnectionPool().takeEntry();
        try {
            String sqlQuery = sqlMap.get(CertQuery.find_subject.name());
            PreparedStatement statement = connection.prepareStatement(sqlQuery);
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
    
    public boolean existsEnabled(String subject) throws SQLException {
        ConnectionEntry connection = storage.getConnectionPool().takeEntry();
        try {
            PreparedStatement statement = connection.prepareStatement(sqlMap.get(CertQuery.enabled.name()));
            statement.setString(1, subject);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            int count = resultSet.getInt(1);
            connection.setOk(true);
            return count > 0;
        } finally {
            storage.getConnectionPool().releaseConnection(connection);
        }
    }
    
    public List<Cert> getList() throws SQLException {
        ConnectionEntry connection = storage.getConnectionPool().takeEntry();
        try {
            List<Cert> list = new ArrayList();
            PreparedStatement statement = connection.prepareStatement(
                    sqlMap.get(CertQuery.list.name()));
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

    public void save(X509Certificate x509Cert) throws SQLException {
        Cert clientCert = findSubject(x509Cert.getSubjectDN().getName());
        if (clientCert == null) {
            clientCert = new Cert();
            clientCert.setCert(x509Cert);
        }
        clientCert.setCert(x509Cert);
        if (clientCert.isStored()) {
            update(clientCert);
        } else {
            insert(clientCert);
        }
    }
}
