/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package venigma.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.security.*;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.TrustManagerFactory;
import vellumexp.logr.Logr;
import vellumexp.logr.LogrFactory;
import venigma.data.CipherStorage;
import venigma.data.KeyInfo;
/**
 *
 * @author evan
 */
public class CipherContext {
    
    Logr logger = LogrFactory.getLogger(getClass());
    CipherRequestAuth requestAuth = new CipherRequestAuth(this);
    CipherConfig config;
    CipherProperties properties;
    SecureRandom sr = new SecureRandom();
    SSLContext sslContext;
    InetSocketAddress address;
    InetAddress inetAddress;
    CipherStorage storage = new CipherStorage(this);
    boolean started = false;
    SSLServerSocket serverSocket;
    Map<KeyInfo, KeyInfo> keyMap = new HashMap();

    public CipherContext() {
    }

    public void config(CipherConfig config, CipherProperties properties) throws Exception {
        this.config = config;
        this.properties = properties;
        storage.init();
        inetAddress = InetAddress.getByName(config.serverIp);
        address = new InetSocketAddress(inetAddress, config.sslPort);
        sslContext = SSLContext.getInstance("TLS");
        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        KeyStore ks = KeyStore.getInstance("JCEKS");
        File keyStoreFile = new File(config.privateKeyStore);
        logger.info(keyStoreFile.getAbsolutePath());
        FileInputStream fis = new FileInputStream(keyStoreFile);
        ks.load(fis, properties.keyStorePassword);
        kmf.init(ks, properties.privateKeyPassword);
        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        KeyStore ts = KeyStore.getInstance("JCEKS");
        FileInputStream trustStoreStream = new FileInputStream(config.trustKeyStore);
        ts.load(trustStoreStream, properties.trustKeyStorePassword);
        tmf.init(ts);
        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), sr);
        logger.info("cipher ssl context initialised", hashCode());
        initServerSocket();
    }

    private void initServerSocket() throws IOException {
        this.serverSocket = (SSLServerSocket) sslContext.getServerSocketFactory().createServerSocket(
                config.sslPort, config.backlog, inetAddress);
        this.serverSocket.setNeedClientAuth(true);
    }

    public SSLServerSocket getServerSocket() {
        return serverSocket;
    }

    public Cipher getCipher(int mode, KeyInfo keyInfo) throws Exception {
        keyInfo = getSecretKey(keyInfo);
        return getCipher(mode, keyInfo.getSecretKey(), keyInfo.getIv());
    }
    
    public KeyInfo getSecretKey(KeyInfo keyInfo) throws Exception {
        logger.info("getSecretKey", keyInfo);
        if (keyMap.containsKey(keyInfo)) {
            keyInfo = keyMap.get(keyInfo);
        } else {
            keyInfo = storage.getKeyInfoStorage().find(keyInfo);
            logger.info("keyInfo", keyInfo);
            keyInfo.decrypt(properties.secretKeyPassword);
            keyMap.put(keyInfo, keyInfo);
        }
        logger.info("getSecretKey iv", keyInfo.getIv());
        return keyInfo;
    }

    public void reviseSecretKey(KeyInfo keyInfo) throws Exception {
        logger.info("reviseSecretKey", keyInfo);
        KeyGenerator aes = KeyGenerator.getInstance("AES");
        aes.init(keyInfo.getKeySize(), sr);
        SecretKey secretKey = aes.generateKey();
        keyInfo.setKey(secretKey, properties.secretKeyPassword, sr);
        keyInfo.incrementRevisionNumber();
        storage.getKeyInfoStorage().insert(keyInfo);
    }

    public void generateSecretKey(KeyInfo keyInfo) throws Exception {
        logger.info("generateSecretKey", keyInfo);
        KeyGenerator aes = KeyGenerator.getInstance("AES");
        aes.init(keyInfo.getKeySize(), sr);
        SecretKey secretKey = aes.generateKey();
        keyInfo.setKey(secretKey, properties.secretKeyPassword, sr);
        storage.getKeyInfoStorage().insert(keyInfo);
    }

    public Cipher getCipher(int mode, SecretKey secretKey, byte[] iv) throws Exception {
        IvParameterSpec ips = new IvParameterSpec(iv);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(mode, secretKey, ips);
        return cipher;
    }
    
    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
        if (true) {
        }
    }

    public CipherStorage getStorage() {
        return storage;
    }

    public CipherConfig getConfig() {
        return config;
    }

    public CipherProperties getProperties() {
        return properties;
    }
}
