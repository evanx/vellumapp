package mobi.servlet.forgot;

import java.io.PrintWriter;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import mobi.entity.Person;
import mobi.session.Servlets;
import mobi.storage.PersonConnection;

/**
 *
 */
public class ForgotHandler {

    HttpServletRequest req;
    HttpServletResponse res;
    PrintWriter writer;
    Connection connection;
    Map responseMap = new HashMap();

    public void handle(HttpServletRequest req, HttpServletResponse res) throws Exception {
        this.req = req;
        this.res = res;
        res.setContentType("text/html");
        writer = res.getWriter();
        try {
            String email = req.getParameter("email");
            connection = Servlets.getConnection();
            PersonConnection personConnection = new PersonConnection(connection);
            Person person = personConnection.find(email);
            if (person == null) {
                responseMap.put("message", "Email not found");
            } else {
                responseMap.put("name", person.getPersonName());
            }
        } catch (Exception e) {
            Servlets.warn(e);
            responseMap.put("message", "There is a technical glitch");
        } finally {
            Servlets.close(connection);
        }
        writer.println(Servlets.buildJson(responseMap));
        writer.close();
    }

}
