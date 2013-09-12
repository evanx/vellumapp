/*
 */
package mobi.fb;

import java.io.IOException;
import java.io.Writer;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author evan.summers
 */
public class FbRedirect {

    public void handle(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("text/plain");
        Writer w = res.getWriter();
        for (String name : req.getParameterMap().keySet()) {
            w.write(name);
            w.write("=");
            w.write(req.getParameter(name));
            w.write("\n");
        }
        w.close();
    }
}
