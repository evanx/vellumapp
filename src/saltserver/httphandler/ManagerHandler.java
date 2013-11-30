
package saltserver.httphandler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import javax.sql.RowSet;
import vellum.connection.ConnectionPool;
import vellum.query.RowSets;
import vellum.storage.StorageException;
import vellum.storage.StorageExceptionType;
import java.io.PrintStream;
import java.sql.*;
import saltserver.app.VaultApp;
import saltserver.app.VaultPageHandler;
import vellum.httpserver.Httpx;
import vellum.logr.Logr;
import vellum.logr.LogrFactory;
import vellum.connection.ConnectionEntry;

/**
 *
 * 
 */
public class ManagerHandler implements HttpHandler {
    
    Logr logger = LogrFactory.getLogger(getClass());
    Httpx httpExchangeInfo;
    VaultPageHandler handler;
    PrintStream out;
    ConnectionPool connectionPool;
    ConnectionEntry connectionEntry;
    Connection connection;
    DatabaseMetaData databaseMetaData;
    int revisionNumber;
    
    public ManagerHandler(VaultApp app) {
        super();
        this.connectionPool = app.getStorage().getConnectionPool();
    }
    
    @Override
    public void handle(HttpExchange httpExchange) {
        handler = new VaultPageHandler(httpExchange);
        httpExchangeInfo = new Httpx(httpExchange);
        out = httpExchangeInfo.getPrintStream();
        logger.info("handle", getClass().getSimpleName(), httpExchangeInfo.getPath());
        try {
            httpExchangeInfo.sendResponse("text/html", true);
            handler.printPageHeader("Manager");
            handle();
        } catch (Exception e) {
            httpExchangeInfo.handleError(e);
        }
        httpExchange.close();
    }
    
    private void handle() throws Exception {
        connectionEntry = connectionPool.takeEntry();
        connection = connectionEntry.getConnection();
        try {
            queryDatabaseTime();
            handler.print(RowSets.getRowSet(connection, "select * from schema_revision"));
            handler.print(RowSets.getRowSet(connection, "select * from secret"));
            printSchema();
            connectionEntry.setOk(true);
        } finally {
            connectionPool.releaseConnection(connectionEntry);
        }
    }

    private Date queryDatabaseTime() throws Exception {
        Statement stmt = connection.createStatement();
        ResultSet rst = stmt.executeQuery("select now()");
        while (rst.next()) {
            return new Date(rst.getTimestamp(1).getTime());
        }
        throw new StorageException(StorageExceptionType.NOT_FOUND);
    }
    
    private int querySchemaRevisionNumber() throws Exception {
        RowSet rowSet = RowSets.getRowSet(connection, "select * from schema_revision order by updated desc");
        if (rowSet.next()) {
            return rowSet.getInt(1);
        }
        throw new StorageException(StorageExceptionType.NOT_FOUND);
    }

    private void printSchema() throws Exception {
        databaseMetaData = connection.getMetaData();
        RowSet rowSet = RowSets.getRowSet(connection, "select * from schema_revision order by updated desc");
        handler.print(rowSet);
        rowSet.first();
        revisionNumber = rowSet.getInt(1);
        out.println("<pre>");
        out.println("databaseProductName " + databaseMetaData.getDatabaseProductName());
        out.println("databaseProductVersion " + databaseMetaData.getDatabaseProductVersion());
        out.println("url " + databaseMetaData.getURL());
        out.println("userName " + databaseMetaData.getUserName());
        out.println("revisionNumber " + querySchemaRevisionNumber());
        out.println("time " + queryDatabaseTime());
        out.println("</pre>");
        ResultSet resultSet = databaseMetaData.getCatalogs();
        while (resultSet.next()) {    
            String catalog = resultSet.getString(1);
            out.printf("<h2>%s</h2>\n", catalog);
            printColumns(catalog);
        }
    }
    
    private void printTables(String catalog) throws Exception {
        ResultSet tablesResultSet = databaseMetaData.getTables(catalog, null, null, null);
        handler.print(tablesResultSet, new String[] {"TABLE_NAME"});
    }
    
    private void printColumns(String catalog) throws Exception {
        ResultSet resultSet = databaseMetaData.getColumns(catalog, "PUBLIC", "%", "%");
        handler.print(resultSet, new String[] {"TABLE_NAME", "COLUMN_NAME", "TYPE_NAME", "COLUMN_SIZE"});
    }

}
