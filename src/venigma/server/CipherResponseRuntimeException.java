/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package venigma.server;

/**
 *
 * @author evan
 */
public class CipherResponseRuntimeException extends RuntimeException {
 
    CipherResponse response;

    public CipherResponseRuntimeException(CipherResponse response) {
        super(getMessage(response));
        this.response = response;
    }

    public CipherResponse getResponse() {
        return response;
    }
        
    public static String getMessage(CipherResponse response) {
        return response.getResponseType().name();
    }
    
}
