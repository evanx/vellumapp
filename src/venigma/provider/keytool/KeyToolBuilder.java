/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package venigma.provider.keytool;

import java.security.SecureRandom;
import vellumexp.logr.Logr;
import vellumexp.logr.LogrFactory;

/**
 *
 * @author evan.summers
 */
public class KeyToolBuilder {
    Logr logger = LogrFactory.getLogger(getClass());
    SecureRandom sr = new SecureRandom();

    KeyToolBuilderConfig config;
    KeyToolBuilderlProperties properties;

    public KeyToolBuilder(KeyToolBuilderConfig config, KeyToolBuilderlProperties properties) {
        this.config = config;
        this.properties = properties;
    }

    public String[] buildKeyToolGenSecKeyArgs(String secretKeyStoreFile, String secretKeyAlias) {
        return new String[] {
            "-genseckey", 
            "-keyalg", config.secretKeyAlg, 
            "-keysize", Integer.toString(config.secretKeySize), 
            "-keystore", secretKeyStoreFile, 
            "-storetype", config.keyStoreType, 
            "-storepass", properties.secretKeyStorePass.toString(),
            "-alias", secretKeyAlias, 
            "-keypass", properties.secretKeyPass.toString()
        };
    }
    
    public String[] buildKeyToolGenKeyPairArgs(String keyStoreFile, String keyAlias, String cn) {
        return new String[] {
            "-genkeypair", 
            "-keyalg", config.keyAlg, 
            "-keystore", keyStoreFile, 
            "-storetype", config.keyStoreType, 
            "-storepass", config.keyStorePass,
            "-alias", keyAlias, 
            "-keypass", properties.privateKeyPass.toString(), 
            "-dname", String.format("CN=%s, OU=Development, O=venigmasecured.com, L=Cape Town, S=WP, C=za", cn)
        };
    }

    public String[] buildKeyToolExportCertArgs(String keyStore, String alias, String certFile) {
        return new String[] {
            "-export", 
            "-keystore", keyStore, 
            "-storetype", config.keyStoreType, 
            "-storepass", config.keyStorePass,
            "-alias", alias, 
            "-keypass", properties.privateKeyPass.toString(), 
            "-file", certFile, 
        };
    }
    
    public String[] buildKeyToolImportCertArgs(String trustStore, String trustAlias, String certFile) {
        return new String[] {
            "-import", 
            "-noprompt",
            "-keystore", trustStore, 
            "-storetype", config.keyStoreType, 
            "-storepass", properties.trustKeyStorePass.toString(),
            "-alias", trustAlias,
            "-file", certFile,
        };
    }

    public String[] buildKeyToolListArgs(String keyStore) throws Exception {
        return new String[] {
            "-list", 
            "-keystore", keyStore, 
            "-storetype", config.keyStoreType, 
            "-storepass", config.keyStorePass
        };
    }
    
}
