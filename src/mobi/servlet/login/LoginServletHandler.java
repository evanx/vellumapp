package mobi.servlet.login;

import vellum.crypto.Passwords;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import mobi.entity.Person;
import mobi.session.Servlets;
import mobi.storage.PersonConnection;
import vellum.crypto.Base64;

/**
 *
 */
public class LoginServletHandler {

    HttpServletRequest req;
    HttpServletResponse res;
    Connection connection;
    Map responseMap = new HashMap();

    public void handle(HttpServletRequest req, HttpServletResponse res) throws Exception {
        this.req = req;
        this.res = res;
        try {
            String email = req.getParameter("email");
            String password = req.getParameter("password");
            connection = Servlets.getConnection();
            PersonConnection personConnection = new PersonConnection(connection);
            Person person = personConnection.find(email);
            if (person == null) {
                responseMap.put("message", "Email not found");
            } else if (!Passwords.matches(password.toCharArray(), Base64.decode(person.getPasswordHash()), Base64.decode(person.getPasswordSalt()))) {
                responseMap.put("message", "Incorrect password");
            } else {
                responseMap.put("name", person.getPersonName());
                Servlets.createSession(req, res, person.getEmail());
            }
        } catch (Exception e) {
            Servlets.warn(e);
            responseMap.put("message", "There is a technical glitch");
        } finally {
            Servlets.close(connection);
        }
        Servlets.writeResponseMap(res, responseMap);
    }

}
