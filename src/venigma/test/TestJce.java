/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package venigma.test;

import java.io.File;
import java.io.FileInputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.Security;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import vellum.logr.Logr;
import vellum.logr.LogrFactory;
import vellum.crypto.util.Base64;
import vellum.util.Bytes;
import venigma.provider.keytool.KeyToolBuilder;
import venigma.server.Ciphers;

/**
 *
 * @author evan
 */
public class TestJce {
    Logr logger = LogrFactory.getLogger(getClass());    

    SecureRandom sr = new SecureRandom();
              
    KeyToolBuilder keyToolBuilder;

    private void run() throws Exception {
        testJceProvider();
        testSecretKey();
    }
    
    private void testJceProvider() throws Exception {
        listProviders();
    }

    public Key generateKey() throws Exception {
        KeyGenerator generator = KeyGenerator.getInstance("AES");
        generator.init(256, sr);
        SecretKey key = generator.generateKey();
        logger.info(key.getAlgorithm(), key.getFormat(), key.getEncoded().length, Base64.encode(key.getEncoded()));
        return key;
    }
    
    private void testSecretKey() throws Exception {
        Key key = generateKey();
        Cipher aesEncryptCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        aesEncryptCipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] iv = aesEncryptCipher.getIV();
        logger.info(Bytes.formatHex(iv));
        Cipher aesDecryptCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec ips = new IvParameterSpec(iv);
        aesDecryptCipher.init(Cipher.DECRYPT_MODE, key, ips);
        String message = "0123456789";
        byte[] encryptedBytes = aesEncryptCipher.doFinal(message.getBytes());
        byte[] decryptedBytes = aesDecryptCipher.doFinal(encryptedBytes);
        logger.info(Bytes.formatHex(encryptedBytes));
        logger.info(new String(decryptedBytes));
        logger.info(Bytes.formatHex(aesEncryptCipher.getParameters().getParameterSpec(IvParameterSpec.class).getIV()));
    }
        
    private void listProviders() {
        for (java.security.Provider prov : Security.getProviders()) {
            logger.info(prov.getName());
        }
    }
    
    public static void main(String[] args) {
        TestJce instance = new TestJce() ;
        try {
            instance.run();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        } finally {
        }
    }
}
