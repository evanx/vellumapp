/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package saltserver.storage.schema;

import vellum.query.RowSets;
import java.io.InputStream;
import java.sql.*;
import javax.sql.RowSet;
import saltserver.app.VaultStorage;
import vellumexp.logr.Logr;
import vellumexp.logr.LogrFactory;
import vellum.connection.ConnectionEntry;

/**
 *
 * @author evan.summers
 */
public class VaultSchema {

    static final int MIN_VERSION_NUMBER = 0;
    static final int CURRENT_VERSION_NUMBER = 1;
    
    Logr logger = LogrFactory.getLogger(VaultSchema.class);
    VaultStorage storage;
    DatabaseMetaData databaseMetaData;

    public VaultSchema(VaultStorage storage) {
        this.storage = storage;
    }

    public void verifySchema() throws Exception {
        if (MIN_VERSION_NUMBER == 0) {
            createSchema();
        } else if (verifySchemaVersion()) {
        } else {
            createSchema();
        }  
    }

    private boolean verifySchemaVersion() throws Exception {
        ConnectionEntry connectionEntry = storage.getConnectionPool().takeEntry();
        Connection connection = connectionEntry.getConnection();
        try {
            databaseMetaData = connection.getMetaData();
            logger.info("databaseProductName " + databaseMetaData.getDatabaseProductName());
            logger.info("databaseProductVersion " + databaseMetaData.getDatabaseProductVersion());
            logger.info("url " + databaseMetaData.getURL());
            logger.info("userName " + databaseMetaData.getUserName());
            RowSet rowSet = RowSets.getRowSet(connection, "select * from schema_revision order by update_time desc");
            rowSet.first();
            int versionNumber = rowSet.getInt(1);
            ResultSet resultSet = databaseMetaData.getCatalogs();
            String catalog = null;
            while (resultSet.next()) {
                catalog = resultSet.getString(1);
                logger.info(catalog);
            }
            connectionEntry.setOk(true);
            return versionNumber >= MIN_VERSION_NUMBER;
        } catch (Exception e) {
            throw e;
        } finally {
            storage.getConnectionPool().releaseConnection(connectionEntry);
        }
    }

    private void createSchema() throws Exception {
        ConnectionEntry connectionEntry = storage.getConnectionPool().takeEntry();
        Connection connection = connectionEntry.getConnection();
        try {
            String sqlScriptName = getClass().getSimpleName() + ".sql";
            InputStream stream = getClass().getResourceAsStream(sqlScriptName);
            logger.info(sqlScriptName);
            byte[] bytes = new byte[stream.available()];
            stream.read(bytes);
            String sql = new String(bytes);
            String[] sqlStatements = sql.split(";");
            for (String sqlStatement : sqlStatements) {
                sqlStatement = sqlStatement.trim();
                if (!sqlStatement.isEmpty()) {
                    logger.verbose(sqlStatement);
                    try {
                        connection.createStatement().execute(sqlStatement);
                    } catch (SQLException e) {
                        logger.warn(e.getMessage());
                    }
                }
            }
            String insertSchemaVersion = "insert into schema_revision (revision_number) values (?)";
            PreparedStatement preparedStatement = connection.prepareStatement(insertSchemaVersion);
            preparedStatement.setInt(1, CURRENT_VERSION_NUMBER);
            preparedStatement.execute();
        } finally {
            storage.getConnectionPool().releaseConnection(connectionEntry);
        }
    }
}
