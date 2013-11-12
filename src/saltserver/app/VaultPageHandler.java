/**
 * Apache Software License 2.0, (c) Copyright 2012 Evan Summers
 * 
 */
package saltserver.app;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import vellum.html.HtmlPrinter;
import vellum.httpserver.Httpx;
import vellum.util.Streams;
import vellum.util.Types;

/**
 *
 * 
 */
public class VaultPageHandler {
    private static int COLUMN_COUNT_LIMIT = 8;
    private static int COLUMN_WIDTH_LIMIT = 32;

    HttpExchange httpExchange;
    Httpx httpExchangeInfo;
    PrintStream out;
        
    public VaultPageHandler(HttpExchange httpExchange) {
        this.httpExchange = httpExchange;
        this.httpExchangeInfo = new Httpx(httpExchange);
        this.out = httpExchangeInfo.getPrintStream();
    }
    
    public void printPageHeader(String title) throws IOException {
        httpExchangeInfo.sendResponse("text/html", true);
        out.println("<html>");
        out.println("<head>");
        out.printf("<title>%s</title>", title);
        out.printf("<style>\n%s\n</style>\n", Streams.readResourceString(getClass(), "style.css"));
        out.println("</head>");
        out.println("<body>");
    }

    public void handleException(Exception e) {
        try {
            httpExchangeInfo.sendResponse("text/html", true);
            out.println("<html>");
            out.println("<head>");
            out.printf("<title>%s</title>", "Error");
            out.printf("<style>\n%s\n</style>\n", Streams.readResourceString(getClass(), "style.css"));
            out.println("</head>");
            out.println("<body>");
            out.println("<pre>");
            e.printStackTrace(out);
        } catch (IOException ioe) {
            e.printStackTrace(System.err);
        }
    }
        
    public void print(ResultSet resultSet) throws Exception {
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
        HtmlPrinter tablePrinter = new HtmlPrinter(out);
        tablePrinter.tableDiv("resultSet");
        tablePrinter.thead();
        for (int i = 1; i <= resultSetMetaData.getColumnCount() && i < COLUMN_COUNT_LIMIT; i++) {
            tablePrinter.th(resultSetMetaData.getColumnName(i));
        }
        tablePrinter.theadClose();
        tablePrinter.tbody();
        while (resultSet.next()) {      
            tablePrinter.tr();
            for (int i = 1; i <= resultSetMetaData.getColumnCount() && i < COLUMN_COUNT_LIMIT; i++) {
                tablePrinter.td(resultSetMetaData.getColumnClassName(i), trim(resultSet.getObject(i)));
            }
            tablePrinter.trClose();
        }
        tablePrinter.tbodyClose();
        tablePrinter.tableDivClose();
    }

    public void print(ResultSet resultSet, String[] columnNames) throws Exception {
        HtmlPrinter h = new HtmlPrinter(out);
        h.table("resultSet");
        h.thead();
        for (int i = 0; i < columnNames.length && i < COLUMN_COUNT_LIMIT; i++) {
            h.th(columnNames[i]);
        }
        h.theadClose();
        h.tbody();
        while (resultSet.next()) {      
            h.tr();
            for (int i = 0; i < columnNames.length && i < COLUMN_COUNT_LIMIT; i++) {
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
            if (string.length() > COLUMN_WIDTH_LIMIT) {
                return string.substring(0, COLUMN_WIDTH_LIMIT);
            }
        }
        return object;
    }

}
