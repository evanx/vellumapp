/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package venigma.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import vellum.logr.Logr;
import vellum.logr.LogrFactory;
import vellum.query.QueryMap;
import vellum.crypto.Base64;
import venigma.server.storage.VStorageException;
import venigma.server.storage.VStorageExceptionType;

/**
 *
 * @author evan.summers
 */
public class KeyInfoStorage {
    static QueryMap sqlMap = new QueryMap(KeyInfoStorage.class);
    static Logr logger = LogrFactory.getLogger(KeyInfoStorage.class);
    Connection connection;

    public KeyInfoStorage(Connection connection) {
        this.connection = connection;
    }
        
    public KeyInfo build(ResultSet resultSet) throws SQLException {
        KeyInfo keyInfo = new KeyInfo();
        keyInfo.setKeyAlias(resultSet.getString("key_alias"));
        keyInfo.setKeySize(resultSet.getInt("key_size"));
        keyInfo.setKeyRevisionNumber(resultSet.getInt("revision_number"));
        keyInfo.setEncryptedKey(Base64.decode(resultSet.getString("data_")));
        keyInfo.setIv(Base64.decode(resultSet.getString("iv")));
        keyInfo.setSalt(Base64.decode(resultSet.getString("salt")));
        return keyInfo;
    }

    public boolean exists(KeyInfo keyId) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(sqlMap.get("exists"));
        statement.setString(1, keyId.getKeyAlias());
        statement.setInt(2, keyId.getKeyRevisionNumber());
        ResultSet resultSet = statement.executeQuery();
        return resultSet.next() && resultSet.getBoolean(1);
    }
    
    public KeyInfo find(KeyInfo keyInfo) throws Exception {
        PreparedStatement statement = connection.prepareStatement(sqlMap.get("find"));
        statement.setString(1, keyInfo.getKeyAlias());
        statement.setInt(2, keyInfo.getKeyRevisionNumber());
        ResultSet resultSet = statement.executeQuery();
        if (!resultSet.next()) {
            throw new VStorageException(VStorageExceptionType.KEY_NOT_FOUND);
        }
        return build(resultSet);
    }
    
    public void insert(KeyInfo keyInfo) throws Exception {
        PreparedStatement statement = connection.prepareStatement(sqlMap.get("insert"));
        statement.setString(1, keyInfo.getKeyAlias());
        statement.setInt(2, keyInfo.getKeyRevisionNumber());
        statement.setInt(3, keyInfo.getKeySize());
        statement.setString(4, Base64.encode(keyInfo.getSalt()));
        statement.setString(5, Base64.encode(keyInfo.getIv()));
        statement.setString(6, Base64.encode(keyInfo.getEncryptedKey()));
        int insertCount = statement.executeUpdate();
        if (insertCount != 1) {
            throw new VStorageException(VStorageExceptionType.KEY_NOT_INSERTED);
        }
    }

    public void delete(KeyInfo keyId) throws Exception {
        PreparedStatement statement = connection.prepareStatement(sqlMap.get("delete"));
        statement.setString(1, keyId.getKeyAlias());
        int updateCount = statement.executeUpdate();
        if (updateCount != 1) {
            throw new VStorageException(VStorageExceptionType.KEY_NOT_DELETED);
        }
    }
    
    public List<KeyInfo> getList() throws SQLException {
        List<KeyInfo> list = new ArrayList();
        PreparedStatement statement = connection.prepareStatement(sqlMap.get("list"));
        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()) {
            list.add(build(resultSet));
        }
        return list;
    }

       
}
