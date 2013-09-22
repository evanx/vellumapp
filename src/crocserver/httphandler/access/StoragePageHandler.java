
package crocserver.httphandler.access;

import crocserver.httphandler.common.AbstractPageHandler;
import vellum.util.Types;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.Date;
import javax.sql.RowSet;
import vellum.html.HtmlPrinter;
import vellum.storage.ConnectionPool;
import vellum.query.RowSets;
import vellum.storage.StorageException;
import vellum.storage.StorageExceptionType;
import crocserver.storage.common.CrocStorage;
import java.sql.Connection;
import vellum.storage.ConnectionEntry;

/**
 *
 * 
 */
public class StoragePageHandler extends AbstractPageHandler {
    public static int COLUMN_LIMIT = 8;

    ConnectionPool connectionPool;
    ConnectionEntry connectionEntry;
    Connection connection;
    DatabaseMetaData databaseMetaData;
    int revisionNumber;
    
    public StoragePageHandler(CrocStorage storage) {
        super();
        this.connectionPool = storage.getConnectionPool();
    }
    
    @Override
    protected void handle() throws Exception {
        connectionEntry = connectionPool.takeEntry();
        connection = connectionEntry.getConnection();
        try {
            queryDatabaseTime();
            print(RowSets.getRowSet(connection, "select * from user_"));
            print(RowSets.getRowSet(connection, "select * from cert"));
            print(RowSets.getRowSet(connection, "select * from org"));
            print(RowSets.getRowSet(connection, "select * from service_record"));
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
        print(rowSet);
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
        print(tablesResultSet, new String[] {"TABLE_NAME"});
    }
    
    private void printColumns(String catalog) throws Exception {
        ResultSet resultSet = databaseMetaData.getColumns(catalog, "PUBLIC", "%", "%");
        print(resultSet, new String[] {"TABLE_NAME", "COLUMN_NAME", "TYPE_NAME", "COLUMN_SIZE"});
    }

    private void print(ResultSet resultSet, String[] columnNames) throws Exception {
        HtmlPrinter h = new HtmlPrinter(out);
        h.table("resultSet");
        h.thead();
        for (int i = 0; i < columnNames.length && i < COLUMN_LIMIT; i++) {
            h.th(columnNames[i]);
        }
        h.theadClose();
        h.tbody();
        while (resultSet.next()) {      
            h.tr();
            for (int i = 0; i < columnNames.length && i < COLUMN_LIMIT; i++) {
                Object value = resultSet.getObject(columnNames[i]);
                h.td(Types.getStyleClass(value.getClass()), value);
            }
            h.trClose();
        }
        h.tbodyClose();
        h.tableClose();
    }

    private Object trim(Object object) {
        if (object instanceof String) {
            String string = (String) object;
            if (string.length() > 32) {
                return string.substring(0, 32);
            }
        }
        return object;
    }
        
    private void print(ResultSet resultSet) throws Exception {
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
        HtmlPrinter tablePrinter = new HtmlPrinter(out);
        tablePrinter.tableDiv("resultSet");
        tablePrinter.thead();
        for (int i = 1; i <= resultSetMetaData.getColumnCount() && i < COLUMN_LIMIT; i++) {
            tablePrinter.th(resultSetMetaData.getColumnName(i));
        }
        tablePrinter.theadClose();
        tablePrinter.tbody();
        while (resultSet.next()) {      
            tablePrinter.tr();
            for (int i = 1; i <= resultSetMetaData.getColumnCount() && i < COLUMN_LIMIT; i++) {
                tablePrinter.td(resultSetMetaData.getColumnClassName(i), trim(resultSet.getObject(i)));
            }
            tablePrinter.trClose();
        }
        tablePrinter.tbodyClose();
        tablePrinter.tableDivClose();
    }
}
