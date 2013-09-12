
package crocserver.storage.schema;

import vellum.printer.StreamPrinter;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.sql.RowSet;
import vellum.storage.ConnectionPool;
import vellum.query.RowSets;
import vellum.storage.ConnectionEntry;

/**
 *
 * 
 */
public class SchemaMigrator {

    static final int MIN_VERSION_NUMBER = 1;
    static final int CURRENT_VERSION_NUMBER = 2;
    
    static final String CATALOG = "PUBLIC";

    StreamPrinter printer;
    Connection connection;
    DatabaseMetaData databaseMetaData;
    ConnectionPool connectionPool;

    public SchemaMigrator(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }
    
    public void migration() throws Exception {
        ConnectionEntry connectionEntry = connectionPool.takeEntry();
        connection = connectionEntry.getConnection();
        try {
            if (MIN_VERSION_NUMBER == 0 || !verifySchema()) {
                createSchema();
            }
            connectionEntry.setOk(true);
        } catch (Exception e) {
            e.printStackTrace(printer.getPrintStream());
        } finally {
            connectionPool.releaseConnection(connectionEntry);
        }  
    }
    
    public boolean verifySchema() throws Exception {
        databaseMetaData = connection.getMetaData();
        RowSet rowSet = RowSets.getRowSet(connection, "select * from meta_revision order by update_time desc");
        rowSet.first();
        int versionNumber = rowSet.getInt(1);
        return versionNumber >= MIN_VERSION_NUMBER;
    }
    
    
    private void createSchema() throws Exception {        
        String sqlScriptName = "create.sql";
        InputStream stream = getClass().getResourceAsStream(sqlScriptName);
        printer.println(getClass().getName() + " " + sqlScriptName);
        byte[] bytes = new byte[stream.available()];
        stream.read(bytes);
        String sql = new String(bytes);
        String[] sqlStatements = sql.split(";");
        for (String sqlStatement : sqlStatements) {
            sqlStatement = sqlStatement.trim();
            if (!sqlStatement.isEmpty()) {
                printer.println(sqlStatement);
                try {
                    connection.createStatement().execute(sqlStatement);
                } catch (SQLException e) {
                    printer.println(e.getMessage());
                }
            }
        }
        String insertSchemaVersion = "insert into meta_revision (revision_number) values (?)";
        PreparedStatement preparedStatement = connection.prepareStatement(insertSchemaVersion);
        preparedStatement.setInt(1, CURRENT_VERSION_NUMBER);
        preparedStatement.execute();
    }
}
