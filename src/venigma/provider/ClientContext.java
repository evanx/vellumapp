/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package venigma.provider;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.KeyStore;
import java.security.SecureRandom;
import javax.net.ssl.*;

/**
 *
 * @author evan.summers
 */
public class ClientContext {
    public static final String CHARSET = "UTF8";
    
    ClientConfig config;
    char[] keyStorePassword;
    char[] trustStorePassword;
    char[] keyPassword;
    InetAddress serverInetAddress;
    InetSocketAddress serverSocketAddress;
    
    KeyStore keyStore;
    KeyStore trustStore;
    SSLContext sslContext;
    KeyManager[] keyManagers;
    TrustManager[] trustManagers;
    SecureRandom secureRandom;
    boolean configured = false; 
    
    public ClientContext() {
    }

    public void config(ClientConfig properties, char[] keyStorePassword, char[] keyPassword, char[] trustStorePassword) throws Exception {
        configured = true;
        this.config = properties;
        this.keyStorePassword = keyStorePassword;
        this.trustStorePassword = trustStorePassword;
        this.keyPassword = keyPassword;
        this.serverInetAddress = InetAddress.getByName(properties.serverIp);
        this.serverSocketAddress = new InetSocketAddress(serverInetAddress, properties.sslPort);
        initKeyManagers();
        initTrustManagers();
        initSSLContext();
        init();
    }
    
    private void init() throws IOException {
        CipherConnection connection = new CipherConnection(this);
        connection.open();
        connection.close();
    }
    
    private void initKeyManagers() throws Exception {
        keyStore = KeyStore.getInstance("JCEKS");
        InputStream inputStream = new FileInputStream(config.keyStore);
        keyStore.load(inputStream, keyStorePassword);
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
        keyManagerFactory.init(keyStore, keyPassword);
        this.keyManagers = keyManagerFactory.getKeyManagers();
    }

    private void initTrustManagers() throws Exception {
        trustStore = KeyStore.getInstance("JCEKS");
        InputStream inputStream = new FileInputStream(config.trustStore);
        trustStore.load(inputStream, trustStorePassword);
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
        trustManagerFactory.init(trustStore);
        this.trustManagers = trustManagerFactory.getTrustManagers();
    }
    
    private void initSSLContext() throws Exception {
        sslContext = SSLContext.getInstance("SSL");
        sslContext.init(keyManagers, trustManagers, secureRandom);
    }

    public SSLContext getSSLContext() {
        return sslContext;
    }
    
    public char[] getKeyStorePassword() {
        return keyStorePassword;
    }

    public Socket createSocket() throws IOException, IllegalArgumentException {
        if (!configured) {
            throw new IllegalArgumentException(ProviderResources.CONTEXT_NOT_INITIALISED);
        }  
        if (false) {
            return new Socket(serverSocketAddress.getAddress(), serverSocketAddress.getPort());
        } else {
            return sslContext.getSocketFactory().createSocket(serverSocketAddress.getAddress(), serverSocketAddress.getPort());
        }
    }
    
}
