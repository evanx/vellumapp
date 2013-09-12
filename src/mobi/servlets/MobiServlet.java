/*
 */
package mobi.servlets;

import vellum.logr.Logr;
import vellum.logr.LogrFactory;
import java.io.IOException;
import java.util.Enumeration;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author evan.summers
 */
public class MobiServlet extends HttpServlet {

    Logr logr = LogrFactory.getLogger(getClass());

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            logr.info(req.getRequestURI());
            Enumeration<String> e = req.getHeaderNames();
            while (e.hasMoreElements()) {
                String headerName = e.nextElement();
                logr.info(headerName + "=" + req.getHeader(headerName));
            }
            if (req.getRequestURI().indexOf("fbredirect.html") > 0) {
                new FbRedirect().service(req, res);
            } else if (req.getRequestURI().indexOf("login.html") > 0) {
                new Login().service(req, res);
            } else if (req.getRequestURI().indexOf("fbredirect.html") > 0) {
                new FbLogin().service(req, res);
            } else {
                new Page("login.html").service(req, res);
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }
}
