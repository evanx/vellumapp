/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package mobi.session;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.Locale;
import java.util.Map;
import javax.servlet.ServletContextEvent;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import mobi.context.MobiContext;
import mobi.mail.Mailer;
import vellumexp.logr.Logr;
import vellumexp.logr.LogrFactory;

/**
 *
 * @author evan.summers
 */
public class Servlets {

    public static Logr logger = LogrFactory.getLogger(Servlets.class);
    static MobiContext context = new MobiContext();
    static Mailer mailer = new Mailer();

    public static void info(HttpServletRequest req) {
        logger.info(req.getServletPath(), req.getParameterNames());
    }

    public static void info(String message, Object... args) {
        logger.info(message, args);
    }

    public static void warn(Exception e) {
        e.printStackTrace(System.err);
    }

    public static String buildJson(Map map) {
        StringBuilder builder = new StringBuilder();
        for (Object key : map.keySet()) {
            if (builder.length() > 0) {
                builder.append(",");
            }
            builder.append(String.format("\n\"%s\" : \"%s\"", key, map.get(key)));
        }
        return "{" + builder.toString() + "\n}";
    }

    public static Connection getConnection() {
        return context.getConnection();
    }

    public static void close(Connection connection) {
        context.close(connection);
    }

    public static void contextInitialized(ServletContextEvent sce) {
        context.contextInitialized(sce);
    }

    public static Mailer getMailer() {
        return mailer;
    }

    public static void createSession(HttpServletRequest req, HttpServletResponse res, String email) {
        MobiSession session = new MobiSession(email);
        new MobiSessionFactory().put(req.getSession(), session);
    }

    public static MobiSessionFactory sessionFactory = new MobiSessionFactory();
    
    public static String getSessionEmail(HttpServletRequest req) {
        MobiSession session = sessionFactory.getSession(req.getSession());
        return session.getEmail();
    }

    public static void clearSession(HttpServletRequest req) {
        sessionFactory.clearSession(req.getSession());
    }
    
    public static void ensureSession(HttpServletRequest req) {
        sessionFactory.getSession(req.getSession());
    }
    
    public static void writeResponseMap(HttpServletResponse res, Map responseMap) throws IOException {
        PrintWriter writer = res.getWriter();
        writer.println(buildJson(responseMap));
        writer.close();
        
    }

    public static String getCurrency() {
        return "ZAR";
    }

    public static Locale getLocale() {
        return new Locale("en", "ZA");
    }

    public static BigDecimal parseCurrency(String amountString) {
        return new BigDecimal(100);
    }

}
