/*
 * Source https://github.com/evanx by @evanxsummers 2011, iPay (Pty) Ltd
 */
package crocserver.httphandler.common;

import vellum.exception.Exceptions;
import vellumexp.logr.Logr;
import vellumexp.logr.LogrFactory;
import vellum.printer.PrintStreamAdapter;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import vellum.httpserver.Httpx;
import vellum.printer.Printer;
import vellum.util.Streams;
import vellum.util.Strings;
import vellum.util.Types;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.zip.GZIPOutputStream;
import vellum.html.HtmlPrinter;

/**
 *
 * @author evan.summers
 */
public abstract class AbstractPageHandler implements HttpHandler {

    protected Logr logger = LogrFactory.getLogger(getClass());
    protected HttpExchange httpExchange;
    protected Httpx httpExchangeInfo;
    protected String urlQuery;
    protected String path;
    protected String[] pathArgs;
    protected Printer out;
    protected HtmlPrinter h;
    protected boolean showMenu = false;
    protected ByteArrayOutputStream baos = null;

    public AbstractPageHandler() {
    }

    public AbstractPageHandler(boolean showMenu) {
        this.showMenu = showMenu;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        this.httpExchange = httpExchange;
        httpExchangeInfo = new Httpx(httpExchange);
        path = httpExchangeInfo.getPath();
        pathArgs = httpExchangeInfo.getPathArgs();
        httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0); 
        if (httpExchangeInfo.getRequestHeader("Agent").contains("wget")) {
            baos = new ByteArrayOutputStream();
            out = new PrintStreamAdapter(baos);
        } else if (httpExchangeInfo.getRequestHeader("Accept").contains("gzip")) {
            httpExchange.getResponseHeaders().set("Content-encoding", "gzip");
            out = new PrintStreamAdapter(new GZIPOutputStream(httpExchange.getResponseBody()));
        } else {
            out = new PrintStreamAdapter(httpExchange.getResponseBody());
        }
        h = new HtmlPrinter(out);
        try {
            printPageHeader();
            if (showMenu) {
                printMenu();
            }
            handle();
            printPageFooter();
        } catch (Exception e) {
            out.printf("<pre>\n");
            e.printStackTrace(System.err);
            out.println(Exceptions.printStackTrace(e));
            out.printf("</pre>\n");
            printPageFooter();
        } finally {
            close();
            if (baos != null) {
                logger.trace("size", baos.size());
                httpExchange.getResponseHeaders().set("Content-length", Integer.toString(baos.size()));
                httpExchange.getResponseBody().write(baos.toByteArray());
                httpExchange.getRequestBody().close();
            }
            httpExchange.close();
        }
    }

    protected abstract void handle() throws Exception;

    protected void printCss() throws IOException {
        String resourceName = getClass().getSimpleName() + ".css";
        printCss(AbstractPageHandler.class, resourceName);
    }

    protected void printCss(Class parentClass, String resourceName) throws IOException {
        out.printf("<style>\n%s\n</style>\n", 
                Streams.readResourceString(parentClass, resourceName));
    }

    protected void printPageHeader() throws IOException {
        out.println("<html>");
        out.println("<head>");
        out.printf("<title>%s</title>", getClass().getSimpleName());
        printCss(AbstractPageHandler.class, "style.css");
        out.println("</head>");
        out.println("<body>");
    }

    protected void printMenu() throws IOException {
        out.printf("<form method='get' action='/search'>\n");
        out.printf("<div class='menuBarDiv'>\n");
        out.printf("<span class='menuItem'><a href='/'><input type='button' value='home'></a></span>\n");
        out.printf("<span class='queryInput'><input name='query' value='%s'/></span>\n",
                Types.formatDisplay(httpExchangeInfo.getParameterMap().get("query")));
        out.printf("</form>\n");
        out.printf("</div>\n");
    }

    public void pre(StringBuilder builder) {
        out.printf("<pre>");
        out.printf(Strings.escapeHtml(builder.toString()));
        out.printf("</pre>\n");
    }

    protected void printPageFooter() throws IOException {
        out.println("</body>");
        out.println("</html>");
    }

    protected void close() throws IOException {
        out.close();
    }
}
