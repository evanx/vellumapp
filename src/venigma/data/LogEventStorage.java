/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package venigma.data;

import java.sql.*;
import vellum.sql.QueryMap;

/**
 *
 * @author evan.summers
 */
public class LogEventStorage {
    static QueryMap sqlMap = new QueryMap(LogEventStorage.class);
    Connection connection;

    public LogEventStorage(Connection connection) {
        this.connection = connection;
    }
        
    public LogEvent newLogEvent(ResultSet resultSet) throws SQLException {
        LogEvent logEvent = new LogEvent();
        logEvent.setId(resultSet.getLong("event_id"));
        logEvent.setMessage(resultSet.getString("message"));
        logEvent.setTimestamp(resultSet.getTimestamp("time_"));
        return logEvent;
    }

    public long insert(LogEvent logEvent) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(sqlMap.get("insert"));
        statement.setString(1, logEvent.getMessage());
        statement.setTimestamp(2, new Timestamp(logEvent.getTimestamp().getTime()));
        int updateCount = statement.executeUpdate();
        if (updateCount != 1) {
            throw new SQLException();
        }
        ResultSet generatedKeys = statement.getGeneratedKeys();
        if (!generatedKeys.next()) {
            throw new SQLException();
        }   
        long id = generatedKeys.getLong(1);
        logEvent.setId(id);
        return id;
    }
        
}
