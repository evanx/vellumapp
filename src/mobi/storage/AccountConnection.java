/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package mobi.storage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import mobi.entity.Account;

/**
 *
 * @author evan.summers
 */
public class AccountConnection {
    static QueryMap sqlMap = new QueryMap(AccountConnection.class);
    Connection connection;

    public AccountConnection(Connection connection) {
        this.connection = connection;
    }
        
    public Account newAccount(ResultSet resultSet) throws SQLException {
        Account account = new Account();
        account.setAccountId(resultSet.getLong("account_id"));
        account.setDescription(resultSet.getString("description"));
        account.setBalance(resultSet.getBigDecimal("balance"));
        account.setBalanceCurrency(resultSet.getString("balance_currency"));
        account.setBalanceAccountTransId(resultSet.getLong("balance_account_trans_id"));
        account.setTimeCreated(resultSet.getTimestamp("time_created"));
        return account;
    }

    public Account find(long accountId) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(sqlMap.get("find by id"));
        statement.setLong(1, accountId);
        ResultSet resultSet = statement.executeQuery();
        if (!resultSet.next()) {
            return null;
        }
        return newAccount(resultSet);
    }
    
    public long insertAccount(Account account) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(sqlMap.get("insert"));
        statement.setString(1, account.getDescription());
        int updateCount = statement.executeUpdate();
        if (updateCount != 1) {
            throw new SQLException();
        }
        ResultSet generatedKeys = statement.getGeneratedKeys();
        if (!generatedKeys.next()) {
            throw new SQLException();
        }   
        long accountId = generatedKeys.getLong(1);
        account.setAccountId(accountId);
        return accountId;
    }
    
    public int updateBalance(Account account) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(sqlMap.get("update balance"));
        statement.setBigDecimal(1, account.getBalance());
        statement.setString(2, account.getBalanceCurrency());
        statement.setLong(3, account.getBalanceAccountTransId());
        statement.setLong(4, account.getAccountId());
        return statement.executeUpdate();
    }
    
        
}
