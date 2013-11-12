/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package venigma.data;

import venigma.entity.AbstractIdEntity;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import vellum.type.ComparableTuple;
import vellum.util.Args;
import venigma.server.storage.VStorageException;
import venigma.server.storage.VStorageExceptionType;

/**
 *
 * @author evan.summers
 */
public class KeyInfo extends AbstractIdEntity {
    String keyAlias;
    int keyRevisionNumber;
    int keySize;
    KeyType keyType;
    byte[] salt;
    byte[] iv;
    byte[] encryptedKey;
    SecretKey secretKey;
    
    public KeyInfo() {
    }
        
    public KeyInfo(String keyAlias, int revisionNumber, int keySize) {
        this.keyAlias = keyAlias;
        this.keyRevisionNumber = revisionNumber;
        this.keySize = keySize;
    }
    
    @Override
    public Comparable getId() {
        return ComparableTuple.create(keyAlias, keyRevisionNumber);
    }

    public void setIv(byte[] iv) {
        this.iv = iv;
    }
    
    public byte[] getIv() {
        return iv;
    }

    public void setSalt(byte[] salt) {
        this.salt = salt;
    }

    public byte[] getSalt() {
        return salt;
    }
    
    public int getKeySize() {
        return keySize;
    }

    public void setKeySize(int keySize) {
        this.keySize = keySize;
    }
    
    public String getKeyAlias() {
        return keyAlias;
    }

    public void setKeyAlias(String keyAlias) {
        this.keyAlias = keyAlias;
    }

    public int getKeyRevisionNumber() {
        return keyRevisionNumber;
    }

    public void setKeyRevisionNumber(int keyRevisionNumber) {
        this.keyRevisionNumber = keyRevisionNumber;
    }
    
    public void incrementRevisionNumber() {
        keyRevisionNumber++;
    }

    public void setKeyType(KeyType keyType) {
        this.keyType = keyType;
    }

    public KeyType getKeyType() {
        return keyType;
    }
    
    public void setEncryptedKey(byte[] encryptedKey) {
        this.encryptedKey = encryptedKey;
    }

    public byte[] getEncryptedKey() {
        return encryptedKey;
    }

    public void setKey(SecretKey secretKey, char[] password, SecureRandom sr) throws Exception {
        salt = new byte[8];
        iv = new byte[16];
        sr.nextBytes(iv);
        sr.nextBytes(salt);
        Cipher cipher = getCipher(Cipher.ENCRYPT_MODE, password);
        encryptedKey = cipher.doFinal(secretKey.getEncoded());
    }
    
    public void decrypt(char[] password) throws Exception {
        Cipher cipher = getCipher(Cipher.DECRYPT_MODE, password);
        byte[] decryptedBytes = cipher.doFinal(encryptedKey);
        secretKey = new SecretKeySpec(decryptedBytes, "AES");
    }
    
    private Cipher getCipher(int mode, char[] password) throws Exception {
        KeySpec spec = new PBEKeySpec(password, salt, 65536, 256);
        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        SecretKey pbeKey = secretKeyFactory.generateSecret(spec);
        SecretKey secretKey = new SecretKeySpec(pbeKey.getEncoded(), "AES");
        IvParameterSpec ips = new IvParameterSpec(iv);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(mode, secretKey, ips);
        return cipher;
    }
        
    public SecretKey getSecretKey() throws VStorageException {
        if (secretKey == null) {
            throw new VStorageException(VStorageExceptionType.KEY_NOT_DECRYPTED);
        }
        return secretKey;
    }

    @Override
    public String toString() {
        return Args.format(keyAlias, keyRevisionNumber, iv);
    }
    
    
}
