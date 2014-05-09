/*
 * Source https://github.com/evanx by @evanxsummers
 */
package encrypto.app;

/**
 *
 * @author evan.summers
 */
public interface PlainHttpxHandler {
    
    public String handle(EncryptoApp app, EncryptoHttpx httpx, EncryptoEntityService es) throws Exception;
}
