/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package venigma.provider;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Date;
import java.util.Enumeration;

/**
 *
 * @author evan.summers
 */
public class KeyStoreSpi extends java.security.KeyStoreSpi {
    ClientContext provider = VProvider.providerContext;
    KeyStore keyStore;

    public KeyStoreSpi() {
    }

    @Override
    public void engineLoad(InputStream stream, char[] password) throws IOException, NoSuchAlgorithmException, CertificateException {
        try {
            keyStore = KeyStore.getInstance("JCEKS");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        password = provider.getKeyStorePassword();
        keyStore.load(stream, password);
    }

    @Override
    public Key engineGetKey(String alias, char[] password) throws NoSuchAlgorithmException, UnrecoverableKeyException {
        try {
            return keyStore.getKey(alias, password);
        } catch (KeyStoreException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Certificate[] engineGetCertificateChain(String alias) {
        try {
            return keyStore.getCertificateChain(alias);
        } catch (KeyStoreException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Certificate engineGetCertificate(String alias) {
        try {
            return keyStore.getCertificate(alias);
        } catch (KeyStoreException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Date engineGetCreationDate(String alias) {
        try {
            return keyStore.getCreationDate(alias);
        } catch (KeyStoreException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void engineSetKeyEntry(String alias, Key key, char[] password, Certificate[] chain) throws KeyStoreException {
    }

    @Override
    public void engineSetKeyEntry(String alias, byte[] key, Certificate[] chain) throws KeyStoreException {
    }

    @Override
    public void engineSetCertificateEntry(String alias, Certificate cert) throws KeyStoreException {
    }

    @Override
    public void engineDeleteEntry(String alias) throws KeyStoreException {
    }

    @Override
    public Enumeration<String> engineAliases() {
        try {
            return keyStore.aliases();
        } catch (KeyStoreException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean engineContainsAlias(String alias) {
        try {
            return keyStore.containsAlias(alias);
        } catch (KeyStoreException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int engineSize() {
        try {
            return keyStore.size();
        } catch (KeyStoreException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean engineIsKeyEntry(String alias) {
        try {
            return keyStore.isKeyEntry(alias);
        } catch (KeyStoreException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean engineIsCertificateEntry(String alias) {
        try {
            return keyStore.isCertificateEntry(alias);
        } catch (KeyStoreException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String engineGetCertificateAlias(Certificate cert) {
        try {
            return keyStore.getCertificateAlias(cert);
        } catch (KeyStoreException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void engineStore(OutputStream stream, char[] password) throws IOException, NoSuchAlgorithmException, CertificateException {
    }
}
