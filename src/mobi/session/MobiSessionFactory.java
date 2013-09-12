/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package mobi.session;

import vellum.util.Calendars;
import java.util.Calendar;
import javax.servlet.http.HttpSession;
import mobi.exception.MobiException;
import mobi.exception.MobiExceptionType;
import vellum.logr.Logr;
import vellum.logr.LogrFactory;

/**
 *
 * @author evan.summers
 */
public class MobiSessionFactory {

    static Logr logger = LogrFactory.getLogger(MobiSessionFactory.class);
    public static final String ATTRIBUTE_NAME = "MobiSession";
    public static final int TIMEOUT_SECONDS = 30;

    public void clearSession(HttpSession httpSession) {
        httpSession.removeAttribute(ATTRIBUTE_NAME);
    }
    
    public MobiSession getSession(HttpSession httpSession) {
        MobiSession mobiSession = (MobiSession) httpSession.getAttribute(ATTRIBUTE_NAME);
        if (mobiSession == null) {
            throw new MobiException(MobiExceptionType.SESSION_NOT_FOUND);
        }
        Calendar now = Calendars.newCalendar();
        Calendar expiryTime = Calendars.newCalendar(mobiSession.getLastRequestMillis());
        expiryTime.add(Calendar.SECOND, TIMEOUT_SECONDS);
        logger.info("get", mobiSession.getEmail(), mobiSession.getLastRequestMillis(), expiryTime.getTimeInMillis(), now.getTimeInMillis());
        if (Calendars.newCalendar().after(expiryTime)) {
            httpSession.removeAttribute(ATTRIBUTE_NAME);
            throw new MobiException(MobiExceptionType.SESSION_EXPIRED);
        }
        mobiSession.setLastRequestMillis(now.getTimeInMillis());
        return mobiSession;
    }

    public void put(HttpSession httpSession, MobiSession mobiSession) {
        logger.info("put", mobiSession.getEmail(), mobiSession.getLastRequestMillis());
        mobiSession.setLastRequestMillis(System.currentTimeMillis());
        httpSession.setAttribute(ATTRIBUTE_NAME, mobiSession);
    }
    
}
