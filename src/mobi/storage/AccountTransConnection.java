/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package mobi.storage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import mobi.entity.AccountTrans;

/**
 *
 * @author evan.summers
 */
public class AccountTransConnection {
    static QueryMap sqlMap = new QueryMap(AccountTransConnection.class);
    Connection connection;

    public AccountTransConnection(Connection connection) {
        this.connection = connection;
    }
        
    public AccountTrans newAccountTrans(ResultSet resultSet) throws SQLException {
        AccountTrans accountTrans = new AccountTrans();
        accountTrans.setDebitAccountId(resultSet.getLong("debit_account_id"));
        accountTrans.setCreditAccountId(resultSet.getLong("credit_account_id"));
        accountTrans.setCurrency(resultSet.getString("currency"));
        accountTrans.setAmount(resultSet.getBigDecimal("amount"));
        accountTrans.setDescription(resultSet.getString("description"));
        accountTrans.setTransType(resultSet.getString("trans_type"));
        accountTrans.setTransStatus(resultSet.getString("trans_status"));
        accountTrans.setTransTime(resultSet.getTimestamp("trans_type"));
        accountTrans.setRequestTime(resultSet.getTimestamp("request_time"));
        accountTrans.setTransTime(resultSet.getTimestamp("trans_time"));
        return accountTrans;
    }

    public AccountTrans find(long accountTransId) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(sqlMap.get("find by id"));
        statement.setLong(1, accountTransId);
        ResultSet resultSet = statement.executeQuery();
        if (!resultSet.next()) {
            return null;
        }
        return newAccountTrans(resultSet);
    }
    
    public long insert(AccountTrans accountTrans) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(sqlMap.get("insert"));
        statement.setLong(1, accountTrans.getDebitAccountId());
        statement.setLong(2, accountTrans.getCreditAccountId());
        statement.setString(3, accountTrans.getDescription());
        statement.setString(4, accountTrans.getTransType());
        statement.setString(5, accountTrans.getTransStatus());
        statement.setString(6, accountTrans.getCurrency());
        statement.setBigDecimal(7, accountTrans.getAmount());
        int updateCount = statement.executeUpdate();
        if (updateCount != 1) {
            throw new SQLException();
        }
        ResultSet generatedKeys = statement.getGeneratedKeys();
        if (!generatedKeys.next()) {
            throw new SQLException();
        }   
        long accountTransId = generatedKeys.getLong(1);
        accountTrans.setAccountTransId(accountTransId);
        return accountTransId;
    }
        
}
