/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package mobi.context;

import java.sql.Connection;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.sql.DataSource;
import vellum.exception.Exceptions;
import vellum.logr.Logr;
import vellum.logr.LogrFactory;

/**
 *
 * @author evan.summers
 */
public class MobiContext implements ServletContextListener {
    static Logr logger = LogrFactory.getLogger(MobiContext.class);
    
    Context ctx;
    DataSource ds;
    String smtpHost;
    String mailLogoUrl;
    
    public MobiContext() {
    }

    public void init(String dataSource) throws Exception {
        ctx = new InitialContext();
        ds = (DataSource) ctx.lookup(dataSource);
    }
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            smtpHost = sce.getServletContext().getInitParameter("smtpHost");
            mailLogoUrl = sce.getServletContext().getInitParameter("mailLogoUrl");
            String dataSource = sce.getServletContext().getInitParameter("dataSource");
            init(dataSource);
            logger.info("contextInitialized", smtpHost);
        } catch (Exception e) {
            throw Exceptions.newRuntimeException(e);
        }
    }
    
    public Connection getConnection() {
        try {
            return ds.getConnection();
        } catch (Exception e) {
            throw Exceptions.newRuntimeException(e);
        }
    }
    
    public static void close(Connection connection) {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (Exception e) {
            throw Exceptions.newRuntimeException(e);
        }
    }

    public String getSmtpHost() {
        return smtpHost;
    }

    public String getMailLogoUrl() {
        return mailLogoUrl;
    }
    
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
    
}
