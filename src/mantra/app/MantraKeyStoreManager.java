/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package mantra.app;

import vellum.security.KeyStores;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

/**
 *
 * @author evan.summers
 */
public class MantraKeyStoreManager {
    String keyStoreLocation;
    KeyStore keyStore;

    public MantraKeyStoreManager(String keyStoreLocation) {
        this.keyStoreLocation = keyStoreLocation;
    }
    
    public void loadKeyStore(char[] keyStorePassword) {
        keyStore = KeyStores.loadKeyStore("JKS", keyStoreLocation, keyStorePassword);
    }
    
    public PrivateKey getPrivateKey(String alias, char[] keyPassword) throws Exception {
        return (PrivateKey) keyStore.getKey(alias, keyPassword);
    }

    public X509Certificate getCert(String alias) throws Exception {
        return (X509Certificate) keyStore.getCertificate(alias);
    }

    public String getKeyStoreLocation() {
        return keyStoreLocation;
    }

    public KeyStore getKeyStore() {
        return keyStore;
    }
        
}
