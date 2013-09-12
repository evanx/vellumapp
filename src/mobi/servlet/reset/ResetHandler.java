
package mobi.servlet.reset;

import vellum.printer.PrintStreamAdapter;
import vellum.printer.StreamPrinter;
import vellum.util.RowSets;
import vellum.util.Types;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.RowSet;
import mobi.session.Servlets;
import vellum.html.HtmlPrinter;

/**
 *
 * 
 */
public class ResetHandler {

    static final int MIN_VERSION_NUMBER = 0;
    static final int CURRENT_VERSION_NUMBER = 2;

    HttpServletRequest req;
    HttpServletResponse res;
    StreamPrinter printer;
    Connection conn;
    DatabaseMetaData databaseMetaData;
    
    public ResetHandler() {
    }
    
    public void processRequest(HttpServletRequest req, HttpServletResponse res) throws Exception {
        this.req = req;
        this.res = res;
        res.setContentType("text/html");
        printer = new PrintStreamAdapter(res.getOutputStream());
        printer.println("<html>");
        printer.println("<head>");
        printer.println("<head>");
        printer.println("<link rel='stylesheet' href='/giftme/monitor.css'/>");
        printer.println("</head>");
        printer.println("<body>");
        try {
            conn = Servlets.getConnection();
            if (MIN_VERSION_NUMBER == 0 || !verifySchema()) {
                createSchema();
            }
        } catch (Exception e) {
            e.printStackTrace(printer.getPrintStream());
        } finally {
            close(conn);
        }
    }

    private boolean verifySchema() throws Exception {
        databaseMetaData = conn.getMetaData();
        printer.println("<pre>");
        printer.println("databaseProductName " + databaseMetaData.getDatabaseProductName());
        printer.println("databaseProductVersion " + databaseMetaData.getDatabaseProductVersion());
        printer.println("url " + databaseMetaData.getURL());
        printer.println("userName " + databaseMetaData.getUserName());
        printer.println("</pre>");
        print(RowSets.getRowSet(conn, "select * from person"));
        RowSet rowSet = RowSets.getRowSet(conn, "select * from schema_version order by time_updated desc");
        print(rowSet);
        rowSet.first();
        int versionNumber = rowSet.getInt(1);
        ResultSet resultSet = databaseMetaData.getCatalogs();
        String catalog = null;
        while (resultSet.next()) {            
            catalog = resultSet.getString(1);
            if (catalog.startsWith("giftme")) {
                printCatalog(catalog);
            }  
        }
        return versionNumber >= MIN_VERSION_NUMBER;
    }
    
    private void printCatalog(String catalog) throws Exception {
        ResultSet tablesResultSet = databaseMetaData.getTables(catalog, null, null, null);
        print(tablesResultSet, new String[] {"TABLE_NAME"});
        ResultSet resultSet = databaseMetaData.getColumns(catalog, "%", "%", "%");
        print(resultSet, new String[] {"TABLE_NAME", "COLUMN_NAME", "TYPE_NAME", "COLUMN_SIZE"});
    }

    private void print(ResultSet resultSet, String[] columnNames) throws Exception {
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
        HtmlPrinter tablePrinter = new HtmlPrinter(printer);
        tablePrinter.table();
        tablePrinter.thead();
        for (int i = 0; i < columnNames.length; i++) {
            tablePrinter.th(columnNames[i]);
        }
        tablePrinter.theadClose();
        tablePrinter.tbody();
        while (resultSet.next()) {      
            tablePrinter.tr();
            for (int i = 0; i < columnNames.length; i++) {
                Object value = resultSet.getObject(columnNames[i]);
                tablePrinter.td(resultSetMetaData.getColumnClassName(i), value);
            }
            tablePrinter.trClose();
        }
        tablePrinter.tbodyClose();
        tablePrinter.tableClose();
    }
    
    private void print(ResultSet resultSet) throws Exception {
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
        HtmlPrinter tablePrinter = new HtmlPrinter(printer);
        tablePrinter.table();
        tablePrinter.thead();
        for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {
            tablePrinter.th(resultSetMetaData.getColumnName(i));
        }
        tablePrinter.theadClose();
        tablePrinter.tbody();
        while (resultSet.next()) {      
            tablePrinter.tr();
            for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {
                tablePrinter.td(resultSetMetaData.getColumnClassName(i), resultSet.getObject(i));
            }
            tablePrinter.trClose();
        }
        tablePrinter.tbodyClose();
        tablePrinter.tableClose();
    }
    
    private void createSchema() throws Exception {        
        String sqlScriptName = "create.sql";
        InputStream stream = getClass().getResourceAsStream(sqlScriptName);
        printer.println("<pre>");
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
                    conn.createStatement().execute(sqlStatement);
                } catch (SQLException e) {
                    printer.println(e.getMessage());
                }
            }
        }
        String insertSchemaVersion = "insert into schema_version (version_number) values (?)";
        PreparedStatement preparedStatement = conn.prepareStatement(insertSchemaVersion);
        preparedStatement.setInt(1, CURRENT_VERSION_NUMBER);
        preparedStatement.execute();
        printer.println("</pre>");
    }

    private void close(Connection conn) {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException ex) {
            warn(ex);
        }
    }

    private void warn(Object object) {
        System.err.println(object);
    }
}

