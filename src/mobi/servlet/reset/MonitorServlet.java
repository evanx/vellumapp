package mobi.servlet.reset;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 */
public class MonitorServlet extends HttpServlet {
    
    public MonitorServlet() throws Exception {
    }
    
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException
    {
        try {
            new MonitorHandler(this).doGet(req, resp);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
    
}
