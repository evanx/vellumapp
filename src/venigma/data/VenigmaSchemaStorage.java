/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package venigma.data;

import java.io.InputStream;
import java.sql.*;
import javax.sql.RowSet;
import vellum.logr.Logr;
import vellum.logr.LogrFactory;
import vellum.util.RowSets;

/**
 *
 * @author evan.summers
 */
public class VenigmaSchemaStorage {
    Logr logger = LogrFactory.getLogger(VenigmaSchemaStorage.class);
    
    static final int MIN_VERSION_NUMBER = 0;
    static final int CURRENT_VERSION_NUMBER = 2;

    Connection connection;
    DatabaseMetaData databaseMetaData;

    public VenigmaSchemaStorage(Connection connection) {
        this.connection = connection;
    }

    public void verifySchema() throws Exception {
        if (MIN_VERSION_NUMBER == 0 || !verifySchemaVersion()) {
             createSchema();
        }
    }
       
    private boolean verifySchemaVersion() throws Exception {
        databaseMetaData = connection.getMetaData();
        logger.info("databaseProductName " + databaseMetaData.getDatabaseProductName());
        logger.info("databaseProductVersion " + databaseMetaData.getDatabaseProductVersion());
        logger.info("url " + databaseMetaData.getURL());
        logger.info("userName " + databaseMetaData.getUserName());
        RowSet rowSet = RowSets.getRowSet(connection, "select * from revision order by time_updated desc");
        rowSet.first();
        int versionNumber = rowSet.getInt(1);
        ResultSet resultSet = databaseMetaData.getCatalogs();
        String catalog = null;
        while (resultSet.next()) {    
            catalog = resultSet.getString(1);
            logger.info(catalog);
        }
        return versionNumber >= MIN_VERSION_NUMBER;
    }
    
    private void createSchema() throws Exception {
        String sqlScriptName = "create.sql";
        InputStream stream = getClass().getResourceAsStream(sqlScriptName);
        logger.info(getClass().getName() + " " + sqlScriptName);
        byte[] bytes = new byte[stream.available()];
        stream.read(bytes);
        String sql = new String(bytes);
        String[] sqlStatements = sql.split(";");
        for (String sqlStatement : sqlStatements) {
            sqlStatement = sqlStatement.trim();
            if (!sqlStatement.isEmpty()) {
                logger.info(sqlStatement);
                try {
                    connection.createStatement().execute(sqlStatement);
                } catch (SQLException e) {
                    logger.warn(e.getMessage());
                }
            }
        }
        String insertSchemaVersion = "insert into meta_revision (revision_number) values (?)";
        PreparedStatement preparedStatement = connection.prepareStatement(insertSchemaVersion);
        preparedStatement.setInt(1, CURRENT_VERSION_NUMBER);
        preparedStatement.execute();
    }
}
