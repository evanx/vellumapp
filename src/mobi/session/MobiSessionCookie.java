/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package mobi.session;

import com.google.gson.Gson;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import vellumexp.logr.Logr;
import vellumexp.logr.LogrFactory;

/**
 *
 * @author evan.summers
 */
public class MobiSessionCookie {
    static Logr logger = LogrFactory.getLogger(MobiSessionCookie.class);
    
    public static final int COOKIE_VERSION = 1;
    public static final String COOKIE_NAME = "MobiSession";
    public static final String COOKIE_NAME_VERSION = "MobiSession_v1";

    private String toCookie(MobiSession session) {
        return new Gson().toJson(session);
    }
    
    private MobiSession newSession(String cookie) {
        return new Gson().fromJson(cookie, MobiSession.class);
    }

    public Cookie newCookie(MobiSession session) {
        Cookie cookie = new Cookie(COOKIE_NAME_VERSION, toCookie(session));
        cookie.setVersion(COOKIE_VERSION);
        return cookie;
    }
    
    private boolean isCookie(Cookie cookie) {
        logger.info("isCookie", cookie.getName(), cookie.getVersion(), cookie.getValue());
        return (cookie.getName().equals(COOKIE_NAME) && cookie.getVersion() == COOKIE_VERSION);
    }

    public MobiSession getSessionCookie(HttpServletRequest req) {
        for (Cookie cookie : req.getCookies()) {
            logger.info("newSession cookie value", cookie.getValue());
            if (cookie.getName().equals(COOKIE_NAME_VERSION)) {
                MobiSession session = newSession(cookie.getValue());
                logger.info("newSession", session.getEmail());
                return session;
            } else if (cookie.getName().startsWith(COOKIE_NAME)) {
                cookie.setMaxAge(0);
            }
        }
        return null;
    }
    
}
