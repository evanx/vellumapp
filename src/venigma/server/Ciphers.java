/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package venigma.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.SecureRandom;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import vellum.logr.Logr;
import vellum.logr.LogrFactory;
import venigma.server.storage.VStorageException;
import venigma.server.storage.VStorageExceptionType;

/**
 *
 * @author evan
 */
public class Ciphers {
    static Logr logger = LogrFactory.getLogger(Ciphers.class);

    public static Key loadKey(String keyStoreFile, String keyAlias, char[] storePass, char[] keyPass) throws Exception {
        File file = new File(keyStoreFile);
        KeyStore keyStore = KeyStore.getInstance("JCEKS", "VProvider");
        keyStore.load(new FileInputStream(file), storePass);
        logger.info("loadKey", keyStore.getType(), keyStore.getProvider().getName());
        Key key = keyStore.getKey(keyAlias, keyPass);
        logger.info(key.getAlgorithm(), key.getFormat());
        return key;
    }
        
    public static void generateAESKey(KeyStore keyStore, String keyAlias, 
            char[] secretKeyPassword, int keySize, SecureRandom sr) throws Exception {
        KeyGenerator aes = KeyGenerator.getInstance("AES");
        aes.init(keySize, sr);
        SecretKey key = aes.generateKey();
        KeyStore.Entry entry = new KeyStore.SecretKeyEntry(key);
        KeyStore.ProtectionParameter prot = new KeyStore.PasswordProtection(secretKeyPassword);
        keyStore.setEntry(keyAlias, entry, prot);
    }

    public static void writeKeyStore(KeyStore keyStore, String fileName, char[] storePassword) throws Exception {
        FileOutputStream stream = new FileOutputStream(fileName);
        keyStore.store(stream, storePassword);
        stream.close();
    }
    
    public static KeyStore loadKeyStore(String fileName, char[] storePassword) throws Exception {
        KeyStore keyStore = KeyStore.getInstance("JCEKS");
        File file = new File(fileName);
        if (!file.exists()) {
            throw new VStorageException(VStorageExceptionType.KEY_NOT_FOUND);
        }
        FileInputStream stream = new FileInputStream(file);
        keyStore.load(stream, storePassword);
        stream.close();
        return keyStore;
    }
    

}
