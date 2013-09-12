/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package mobi.exception;

/**
 *
 * @author evan.summers
 */
public class MobiException extends RuntimeException {
    MobiExceptionType exceptionType;

    public MobiException(MobiExceptionType exceptionType) {
        this.exceptionType = exceptionType;
    }

    public MobiExceptionType getExceptionType() {
        return exceptionType;
    }

    @Override
    public String getMessage() {
        return Resources.getString(getClass(), exceptionType.name());
    }
        
}
