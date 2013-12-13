/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package mobi.session;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import mobi.exception.MobiException;
import vellum.logr.Logr;
import vellum.logr.LogrFactory;

/**
 *
 * @author evan.summers
 */
public class MobiFilter implements Filter {

    static Logr logger = LogrFactory.getLogger(MobiFilter.class);
    FilterConfig filterConfig = null;

    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        PrintStream out = System.out;
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        out.println("request " + req.getServletPath());
        Enumeration<String> parameterNames = request.getParameterNames();
        if (parameterNames.hasMoreElements()) {
            while (parameterNames.hasMoreElements()) {
                String name = parameterNames.nextElement();
                String value = request.getParameter(name);
                out.printf("param: %s\n", name);
            }
        } else {
        }
        Map responseMap = new HashMap();
        try {
            if (req.getServletPath().startsWith("/secure")) {
                Servlets.ensureSession(req);
            } else if (req.getServletPath().startsWith("/session")) {
                Servlets.clearSession(req);
            }
            out.printf("filter servlet %s\n", req.getServletPath());
            filterChain.doFilter(request, response);
            out.printf("filter servlet complete %s\n", req.getServletPath());
        } catch (MobiException me) {
            responseMap.put("message", me.getMessage());
            Servlets.writeResponseMap(res, responseMap);
        } catch (Exception e) {
            logger.warn(e);
            responseMap.put("message", "There is a technical glitch");
            Servlets.writeResponseMap(res, responseMap);
        } finally {
        }
    }

    private boolean requiresSession(HttpServletRequest req) {
        return false;
    }
}