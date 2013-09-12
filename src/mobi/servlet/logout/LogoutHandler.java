package mobi.servlet.logout;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import mobi.entity.Person;
import mobi.exception.MobiException;
import mobi.session.Servlets;
import mobi.storage.PersonConnection;

/**
 *
 */
public class LogoutHandler {

    HttpServletRequest req;
    HttpServletResponse res;
    Connection connection;
    Map responseMap = new HashMap();

    public void handle(HttpServletRequest req, HttpServletResponse res) throws Exception {
        this.req = req;
        this.res = res;
        try {
            handle();
        } catch (MobiException me) {
            responseMap.put("message", me.getMessage());
        } catch (Exception e) {
            Servlets.warn(e);
            responseMap.put("message", "There is a technical glitch");
        } finally {
            Servlets.close(connection);
        }
        Servlets.writeResponseMap(res, responseMap);
    }
    
    protected void handle() throws Exception {
        String email = Servlets.getSessionEmail(req);
        Servlets.info("logout", email);
        if (email == null) {
            responseMap.put("message", "Not logged in");
            return;
        }
        connection = Servlets.getConnection();
        PersonConnection personConnection = new PersonConnection(connection);
        Person person = personConnection.find(email);
        if (person == null) {
            responseMap.put("message", "Session not found");
        } else {
            responseMap.put("name", person.getPersonName());
        }
    }

}
