/*
 */
package mobi.servlets;

import vellumexp.logr.Logr;
import vellumexp.logr.LogrFactory;
import vellum.util.Streams;
import java.io.IOException;
import java.io.Writer;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author evan.summers
 */
public class Login {

    Logr logr = LogrFactory.getLogger(getClass());
    HtmlBuilder builder = new HtmlBuilder(Streams.loadResourceString(getClass(), "login.html"));
    HttpServletRequest req;
    HttpServletResponse res;

    protected void service(HttpServletRequest req, HttpServletResponse res) throws IOException {
        this.req = req;
        this.res = res;
        res.setContentType("text/html");
        Writer w = res.getWriter();
        w.write(builder.toString());
        w.close();
    }
}
