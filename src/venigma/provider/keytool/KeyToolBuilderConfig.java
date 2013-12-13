/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package venigma.provider.keytool;

/**
 *
 * @author evan.summers
 */
public class KeyToolBuilderConfig {

    String keyStoreType = "JCEKS";
    String keyStorePass = "storepass";
    String keyAlg = "DSA";
    String secretKeyAlg = "AES";
    int secretKeySize = 256;

    public String getKeyAlg() {
        return keyAlg;
    }

    public void setKeyAlg(String keyAlg) {
        this.keyAlg = keyAlg;
    }

    public String getKeyStorePass() {
        return keyStorePass;
    }

    public void setKeyStorePass(String keyStorePass) {
        this.keyStorePass = keyStorePass;
    }

    public String getKeyStoreType() {
        return keyStoreType;
    }

    public void setKeyStoreType(String keyStoreType) {
        this.keyStoreType = keyStoreType;
    }

    public String getSecretKeyAlg() {
        return secretKeyAlg;
    }

    public void setSecretKeyAlg(String secretKeyAlg) {
        this.secretKeyAlg = secretKeyAlg;
    }

    public int getSecretKeySize() {
        return secretKeySize;
    }

    public void setSecretKeySize(int secretKeySize) {
        this.secretKeySize = secretKeySize;
    }

    
}
