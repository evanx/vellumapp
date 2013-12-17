
package vellum.security;

import java.io.OutputStream;
import java.io.PrintStream;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

/**
 *
 * @author evans
 */
public class PKCS12KeyStore {

    public void engineSetKeyEntry(String alias, PrivateKey privateKey, char[] password, 
            X509Certificate[] chain) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public PrivateKey engineGetKey(String alias, char[] password) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public void engineStore(PrintStream printStream, char[] password) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Certificate[] engineGetCertificateChain(String alias) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void engineStore(OutputStream stream, char[] password) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
