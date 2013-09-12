/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package venigma.test;

/**
 *
 * @author evan
 */
public class TestProperties {
    String client0Cert = "/evans/test/client0.cert";
    String client0KeyAlias = "client0";
    String client0CertAlias = "client0";
    String client0Cn = "evan";
    String client0KeyStoreFile = "/evans/test/client0KeyStore.ks";
    String client0TrustStoreFile = "/evans/test/client0TrustStore.ks";

    String client1Cert = "/evans/test/client1.cert";
    String client1KeyAlias = "client1";
    String client1CertAlias = "client1";
    String client1Cn = "client1";
    String client1KeyStoreFile = "/evans/test/client1KeyStore.ks";
    String client1TrustStoreFile = "/evans/test/client1TrustStore.ks";

    String client2Cert = "/evans/test/client2.cert";
    String client2KeyAlias = "client2";
    String client2CertAlias = "client2";
    String client2Cn = "client2";
    String client2KeyStoreFile = "/evans/test/client2KeyStore.ks";
    String client2TrustStoreFile = "/evans/test/client2TrustStore.ks";
        
    String providerCert = "/evans/test/provider.cert";
    String providerKeyAlias = "provider";
    String providerCertAlias = "provider";
    String providerCn = "provider";
    String providerKeyStoreFile = "/evans/test/providerKeyStore.ks";
    String providerTrustStoreFile = "/evans/test/providerTrustStore.ks";

    String cipherCert = "/evans/test/cipher.cert";
    String cipherPrivateKeyStoreFile = "/evans/test/cipherPrivateKeyStore.ks";
    String cipherTrustKeyStoreFile = "/evans/test/cipherTrustStore.ks";
    String cipherSecretKeyStoreFile = "/evans/test/cipherSecretKeyStore.ks";

    String keyStoreType = "JCEKS";
    String keyStorePass = "storepass";
    String trustKeyStorePass = "storepass";
    String secretKeyStorePass = "storepass";
    String privateKeyPass = "keypass";
    String secretKeyPass = "keypass";
    String secretKeyAlias = "secret0";
    String keyAlg = "DSA";
    String secretKeyAlg = "AES";
    int secretKeySize = 256;
    
    String username0 = "evan";
    String username1 = "bryan";
    String username2 = "patrick";
}
