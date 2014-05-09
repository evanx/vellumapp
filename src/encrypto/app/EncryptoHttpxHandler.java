/*
 * Source https://github.com/evanx by @evanxsummers
 */
package encrypto.app;

import vellum.jx.JMap;

/**
 *
 * @author evan.summers
 */
public interface EncryptoHttpxHandler {
    
    public JMap handle(EncryptoApp app, EncryptoHttpx httpx, EncryptoEntityService es) throws Exception;
}
