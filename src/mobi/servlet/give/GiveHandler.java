package mobi.servlet.give;

import vellum.util.Strings;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import mobi.entity.Account;
import mobi.entity.AccountTrans;
import mobi.entity.Person;
import mobi.session.Servlets;
import mobi.storage.AccountConnection;
import mobi.storage.AccountTransConnection;
import mobi.storage.PersonConnection;

/**
 *
 */
public class GiveHandler {

    HttpServletRequest req;
    HttpServletResponse res;
    Connection connection;
    Map responseMap = new HashMap();
    String email;
    String amountString;
    BigDecimal amount;
    
    public void handle(HttpServletRequest req, HttpServletResponse res) throws Exception {
        this.req = req;
        this.res = res;
        email = req.getParameter("email");
        amountString = req.getParameter("amount");
        amount = Servlets.parseCurrency(amountString);
        if (validate()) {
            process();
        }
        Servlets.writeResponseMap(res, responseMap);
    }

    private boolean validate() throws Exception {
        if (Strings.isEmpty(email)) {
            responseMap.put("message", "Invalid email address");
            return false;
        }
        return true;
    }
    
    private void process() throws Exception {
        connection = Servlets.getConnection();
        AccountConnection accountConnection = new AccountConnection(connection);
        AccountTransConnection accountTransConnection = new AccountTransConnection(connection);
        PersonConnection personConnection = new PersonConnection(connection);
        String email = Servlets.getSessionEmail(req);
        Person debtor = personConnection.find(email);
        Person beneficiary = personConnection.find(email);
        Account creditAccount = null; 
        if (beneficiary != null) {
            responseMap.put("name", beneficiary.getPersonName());
            creditAccount = accountConnection.find(beneficiary.getAccountId());
        } else {
            responseMap.put("name", email);
            creditAccount = new Account();
            creditAccount.setDescription(email);
            accountConnection.insertAccount(creditAccount);
            beneficiary = new Person();
            beneficiary.setEmail(email);
            beneficiary.setPersonName(email);
            beneficiary.setAccountId(creditAccount.getAccountId());
            personConnection.insertPerson(beneficiary);
        }
        AccountTrans accountTrans = new AccountTrans();
        accountTrans.setDebitAccountId(debtor.getAccountId());
        accountTrans.setCreditAccountId(creditAccount.getAccountId());
        accountTrans.setCurrency(Servlets.getCurrency());
        accountTrans.setTransType("GIFT");
        accountTrans.setTransStatus("PENDING");
        accountTrans.setDescription(String.format("GIFT TO %s", beneficiary.getPersonName()));
        accountTrans.setAmount(amount);
        accountTransConnection.insert(accountTrans);
    }
}

    