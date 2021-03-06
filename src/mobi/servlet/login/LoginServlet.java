package mobi.servlet.login;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 */
public class LoginServlet extends HttpServlet
{
    @Override
    public void service(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException
    {
        try {
            new LoginServletHandler().handle(req, resp);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
    
}
