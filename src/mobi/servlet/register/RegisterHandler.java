package mobi.servlet.register;

import java.io.PrintWriter;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import mobi.entity.Account;
import mobi.entity.Person;
import mobi.session.Servlets;
import mobi.storage.AccountConnection;
import mobi.storage.PersonConnection;

/**
 *
 */
public class RegisterHandler {

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
            RegisterBean bean = new RegisterBean();
            bean.setEmail(req.getParameter("email"));
            bean.setName(req.getParameter("name"));
            bean.setPassword(req.getParameter("password"));
            connection = Servlets.getConnection();
            PersonConnection personConnection = new PersonConnection(connection);
            if (personConnection.existsPerson(bean.getEmail())) {
                responseMap.put("message", "User with that email already exists");
            } else {
                AccountConnection accountConnection = new AccountConnection(connection);
                Account account = new Account();
                account.setDescription(bean.getEmail());
                accountConnection.insertAccount(account);
                Person person = new Person();
                person.setEmail(bean.getEmail());
                person.setPersonName(bean.getName());
                person.setPasswordHash(bean.hashPassword());
                person.setPasswordSalt(bean.encodeSalt());
                person.setAccountId(account.getAccountId());
                personConnection.insertPerson(person);
                responseMap.put("name", bean.getName());
                Servlets.createSession(req, res, person.getEmail());
                Servlets.getMailer().startWelcomeEmail(bean.getEmail(), bean.getName());
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
