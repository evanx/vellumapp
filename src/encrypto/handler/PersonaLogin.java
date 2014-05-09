/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package encrypto.handler;

import encrypto.app.EncryptoApp;
import encrypto.app.EncryptoCookie;
import encrypto.app.EncryptoEntityService;
import encrypto.app.EncryptoHttpx;
import encrypto.app.EncryptoHttpxHandler;
import encrypto.entity.Person;
import encrypto.persona.PersonaInfo;
import encrypto.persona.PersonaVerifier;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.jx.JMap;
import vellum.util.Lists;

/**
 *
 * @author evan.summers
 */
public class PersonaLogin implements EncryptoHttpxHandler {

    static Logger logger = LoggerFactory.getLogger(PersonaLogin.class);
    String assertion;
    int timezoneOffset;
    EncryptoCookie cookie;
    
    @Override
    public JMap handle(EncryptoApp app, EncryptoHttpx httpx, EncryptoEntityService es) 
            throws Exception {
        JMap map = httpx.parseJsonMap();
        timezoneOffset = map.getInt("timezoneOffset");
        logger.trace("timezoneOffset {}", timezoneOffset);
        assertion = map.getString("assertion");
        if (EncryptoCookie.matches(httpx.getCookieMap())) {
            cookie = new EncryptoCookie(httpx.getCookieMap());
        }
        PersonaInfo userInfo = new PersonaVerifier(app, cookie).getPersonaInfo(
                httpx.getHostUrl(), assertion);
        logger.trace("persona {}", userInfo);
        String email = userInfo.getEmail();
        Person person = es.findPerson(email);
        if (person == null) {
            person = new Person(email);
            person.setEnabled(true);
            person.setLoginTime(new Date());
            es.persist(person);
            logger.info("insert user {}", email);
        } else {
            person.setEnabled(true);
            person.setLoginTime(new Date());
        }
        cookie = new EncryptoCookie(person.getEmail(), person.getLabel(),
                person.getLoginTime().getTime(), timezoneOffset, assertion);
        JMap cookieMap = cookie.toMap();
        logger.trace("cookie {}", cookieMap);
        cookieMap.put("timezoneOffset", timezoneOffset);
        httpx.setCookie(cookieMap, EncryptoCookie.MAX_AGE_MILLIS);
        return cookieMap;
    }
}
