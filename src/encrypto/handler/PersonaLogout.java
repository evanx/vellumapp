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
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.jx.JMap;

/**
 *
 * @author evan.summers
 */
public class PersonaLogout implements EncryptoHttpxHandler {

    static Logger logger = LoggerFactory.getLogger(PersonaLogout.class);
    EncryptoCookie cookie;
    
    @Override
    public JMap handle(EncryptoApp app, EncryptoHttpx httpx, EncryptoEntityService es) 
            throws Exception {
        logger.info("handle", getClass().getSimpleName(), httpx.getPath());
        try {
            String email = httpx.parseJsonMap().getString("email");
            if (EncryptoCookie.matches(httpx.getCookieMap())) {
                cookie = new EncryptoCookie(httpx.getCookieMap());
                logger.debug("cookie {}", cookie.getEmail());
                if (!cookie.getEmail().equals(email)) {
                    logger.warn("email {}", email);
                }
                if (app.getProperties().isTesting()) {
                    logger.info("testing mode: ignoring logout");
                } else {
                    logger.info("cookie", cookie.getEmail());
                    Person user = es.findPerson(cookie.getEmail());
                    user.setLogoutTime(new Date());
                }
            }
            return new JMap();
        } finally {
            httpx.setCookie(EncryptoCookie.emptyMap(), EncryptoCookie.MAX_AGE_MILLIS);
        }
    }
}
