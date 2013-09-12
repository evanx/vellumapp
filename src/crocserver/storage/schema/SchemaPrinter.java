
package crocserver.storage.schema;

import java.io.PrintStream;
import vellum.printer.StreamPrinter;
import vellum.util.Types;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import javax.sql.RowSet;
import vellum.html.HtmlPrinter;
import vellum.printer.PrintStreamAdapter;
import vellum.storage.ConnectionPool;
import vellum.query.RowSets;
import vellum.storage.ConnectionEntry;

/**
 *
 * 
 */
public class SchemaPrinter {

    static final int MIN_VERSION_NUMBER = 1;
    static final int CURRENT_VERSION_NUMBER = 2;

    StreamPrinter printer;
    Connection connection;
    DatabaseMetaData databaseMetaData;
    
    public SchemaPrinter() {
    }
    
    public void handle(ConnectionPool connectionPool, PrintStream out, String catalog) throws Exception {
        this.printer = new PrintStreamAdapter(out);
        printer.println("<html>");
        printer.println("<head>");
        printer.println("<head>");
        printer.println("<link rel='stylesheet' href='monitor.css'/>");
        printer.println("</head>");
        printer.println("<body>");
        ConnectionEntry connectionEntry = connectionPool.takeEntry();
        Connection connection = connectionEntry.getConnection();
        boolean ok = false;
        try {
            RowSet rowSet = RowSets.getRowSet(connection, "select * from schema_revision order by updated desc");
            print(rowSet);
            rowSet.first();
            int revisionNumber = rowSet.getInt(1);
            printer.println(String.format("<b>revisionNumber %d</b>", revisionNumber));
            databaseMetaData = connection.getMetaData();
            printer.println("<pre>");
            printer.println("databaseProductName " + databaseMetaData.getDatabaseProductName());
            printer.println("databaseProductVersion " + databaseMetaData.getDatabaseProductVersion());
            printer.println("url " + databaseMetaData.getURL());
            printer.println("userName " + databaseMetaData.getUserName());
            printer.println("</pre>");
            ResultSet resultSet = databaseMetaData.getCatalogs();
            while (resultSet.next()) {
                String catalogString = resultSet.getString(1);
                if (catalogString.startsWith(catalog)) {
                    printCatalog(catalog);
                }
            }
            connectionEntry.setOk(true);
        } catch (Exception e) {
            e.printStackTrace(printer.getPrintStream());
        } finally {
            connectionPool.releaseConnection(connectionEntry);
        }
    }

    private void printCatalog(String catalog) throws Exception {
        ResultSet tablesResultSet = databaseMetaData.getTables(catalog, null, null, null);
        print(tablesResultSet, new String[] {"TABLE_NAME"});
        ResultSet resultSet = databaseMetaData.getColumns(catalog, "%", "%", "%");
        print(resultSet, new String[] {"TABLE_NAME", "COLUMN_NAME", "TYPE_NAME", "COLUMN_SIZE"});
    }

    private void print(ResultSet resultSet, String[] columnNames) throws Exception {
        HtmlPrinter out = new HtmlPrinter(printer);
        out.table("resultSet");
        out.thead();
        for (int i = 0; i < columnNames.length; i++) {
            out.th(columnNames[i]);
        }
        out.theadClose();
        out.tbody();
        while (resultSet.next()) {      
            out.tr();
            for (int i = 0; i < columnNames.length; i++) {
                Object value = resultSet.getObject(columnNames[i]);
                out.td(Types.getStyleClass(value.getClass()), value);
            }
            out.trClose();
        }
        out.tbodyClose();
        out.tableClose();
    }
    
    private void print(ResultSet resultSet) throws Exception {
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
        HtmlPrinter out = new HtmlPrinter(printer);
        out.table("resultSet");
        out.thead();
        for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {
            out.th(resultSetMetaData.getColumnName(i));
        }
        out.theadClose();
        out.tbody();
        while (resultSet.next()) {      
            out.tr();
            for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {
                out.td(resultSetMetaData.getColumnClassName(i), resultSet.getObject(i));
            }
            out.trClose();
        }
        out.tbodyClose();
        out.tableClose();
    }
}
