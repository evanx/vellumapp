/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package venigma.test;

import java.io.File;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import sun.security.tools.KeyTool;
import vellumexp.logr.Logr;
import vellumexp.logr.LogrFactory;
import vellum.util.Streams;
import venigma.data.AdminRole;
import venigma.data.AdminUser;
import venigma.provider.CipherConnection;
import venigma.provider.ClientConfig;
import venigma.provider.ClientContext;
import venigma.provider.VProvider;
import venigma.provider.keytool.KeyToolBuilder;
import venigma.provider.keytool.KeyToolBuilderConfig;
import venigma.provider.keytool.KeyToolBuilderlProperties;
import venigma.server.CipherConfig;
import venigma.server.CipherContext;
import venigma.server.CipherProperties;
import venigma.server.CipherRequest;
import venigma.server.CipherRequestType;
import venigma.server.CipherResponse;
import venigma.server.CipherResponseType;
import venigma.server.CipherServer;
import venigma.server.VCipher;

/**
 *
 * @author evan
 */
public class TestVenigma implements Runnable {

    Logr logger = LogrFactory.getLogger(getClass());
    TestProperties properties = new TestProperties();
    SecureRandom sr = new SecureRandom();
    ClientConfig providerConfig = new ClientConfig();
    ClientContext providerContext = VProvider.providerContext;
    VProvider provider = new VProvider();
    ClientConfig client0Config = new ClientConfig();
    ClientContext client0Context = new ClientContext();
    ClientConfig client1Config = new ClientConfig();
    ClientContext client1Context = new ClientContext();
    ClientConfig client2Config = new ClientConfig();
    ClientContext client2Context = new ClientContext();
    CipherConfig cipherConfig = new CipherConfig();
    CipherProperties cipherProperties = new CipherProperties();
    CipherContext cipherContext = new CipherContext();
    CipherServer server = new CipherServer();
    KeyToolBuilder keyToolBuilder;
    
    @Override
    public void run() {
        try {
            process();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }
    
    public void process() throws Exception {
        setProperties();
        initKeyToolBuilder();
        createKeystores();
        testCipherConnection();
    }
    
    private void testCipherConnection() throws Exception {
        cipherProperties.userList.addAll(buildUserList());
        cipherContext.config(cipherConfig, cipherProperties);
        cipherContext.getStorage().getAdminUserStorage().insertAll(buildUserList());
        server.config(cipherContext);
        server.start();
        configClientContext();
        sendStartRequest();
        sendGenerateKeyRequest();
        configProviderContext();
        testCipher();
        server.close();
    }
    
    private void configProviderContext() throws Exception {
        providerContext.config(providerConfig,
                properties.keyStorePass.toCharArray(), properties.privateKeyPass.toCharArray(),
                properties.keyStorePass.toCharArray());
    }
    
    private void configClientContext() throws Exception {
        client0Context.config(client0Config,
                properties.keyStorePass.toCharArray(), properties.privateKeyPass.toCharArray(),
                properties.keyStorePass.toCharArray());
    }

    private CipherResponse sendRegisterCipher() throws Exception {
        return new CipherResponse(CipherResponseType.OK);
    }

    private CipherResponse sendAddUser() throws Exception {
        return new CipherResponse(CipherResponseType.OK);
    }
    
    private CipherResponse sendRegisterUser() throws Exception {
        return new CipherResponse(CipherResponseType.OK);
    }
    
    private CipherResponse sendStartRequest() throws Exception {
        CipherConnection clientConnection = new CipherConnection(client0Context);
        CipherRequest request = new CipherRequest(CipherRequestType.START);
        request.setPassword(properties.secretKeyPass.toCharArray());
        CipherResponse response = clientConnection.sendCipherRequest(request);
        if (response.getResponseType() != CipherResponseType.OK) {
            logger.warn("client start response", response);
            System.exit(1);
        }
        return response;
    }
    
    private CipherResponse sendGenerateKeyRequest() throws Exception {
        CipherConnection clientConnection = new CipherConnection(client0Context);
        CipherRequest request = new CipherRequest(CipherRequestType.GENERATE_KEY);
        request.setKeyAlias(VProvider.providerContext.getKeyAlias());
        request.setKeySize(256);
        request.setPassword(properties.secretKeyPass.toCharArray());
        CipherResponse response = clientConnection.sendCipherRequest(request);
        if (response.getResponseType() != CipherResponseType.OK) {
            logger.warn("client generate key response", response);
            System.exit(1);
        }
        return response;
    }
    
    private void testCipher() throws Exception {
        VCipher cipher = new VCipher();
        cipher.init(Cipher.ENCRYPT_MODE, null, sr);
        String datum = "12345678901234567890";
        logger.info(datum);
        byte[] bytes = datum.getBytes();
        byte[] encryptedBytes = cipher.doFinal(bytes, 0, bytes.length);
        byte[] iv = cipher.getIV();
        logger.info("length", encryptedBytes.length);
        cipher.init(Cipher.DECRYPT_MODE, null, new IvParameterSpec(iv), sr);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes, 0, bytes.length);
        logger.info(new String(decryptedBytes));
    }
    
    private void setProperties() {
        client0Config.keyStore = properties.client0KeyStoreFile;
        client0Config.cn = properties.client0Cn;
        client0Config.keyAlias = properties.client0KeyAlias;
        client0Config.trustStore = properties.client0TrustStoreFile;
        client1Config.keyAlias = properties.client1KeyAlias;
        client1Config.keyStore = properties.client1KeyStoreFile;
        client1Config.cn = properties.client1Cn;
        client1Config.trustStore = properties.client1TrustStoreFile;
        client2Config.keyStore = properties.client2KeyStoreFile;
        client2Config.cn = properties.client2Cn;
        client2Config.keyAlias = properties.client2KeyAlias;
        client2Config.trustStore = properties.client2TrustStoreFile;
        providerConfig.keyAlias = properties.providerKeyAlias;
        providerConfig.cn = properties.providerCn;
        providerConfig.keyStore = properties.providerKeyStoreFile;
        providerConfig.trustStore = properties.providerTrustStoreFile;
        cipherConfig.privateKeyStore = properties.cipherPrivateKeyStoreFile;
        cipherConfig.trustKeyStore = properties.cipherTrustKeyStoreFile;
        cipherConfig.secretKeyStore = properties.cipherSecretKeyStoreFile;
        cipherProperties.keyStorePassword = properties.keyStorePass.toCharArray();
        cipherProperties.trustKeyStorePassword = properties.trustKeyStorePass.toCharArray();
        cipherProperties.privateKeyPassword = properties.privateKeyPass.toCharArray();
        cipherProperties.secretKeyStorePassword = properties.secretKeyStorePass.toCharArray();
        cipherProperties.secretKeyPassword = properties.secretKeyPass.toCharArray();
        cipherProperties.databaseUserPassword = null;
        cipherProperties.databaseStorePassword = null;
    }
    
    private void initKeyToolBuilder() {
        KeyToolBuilderConfig keyConfig = new KeyToolBuilderConfig();
        KeyToolBuilderlProperties keyProperties = new KeyToolBuilderlProperties();
        keyProperties.setPrivateKeyPass(properties.privateKeyPass);
        keyProperties.setTrustKeyStorePass(properties.trustKeyStorePass);
        keyProperties.setSecretKeyStorePass(properties.secretKeyStorePass);
        keyProperties.setSecretKeyPass(properties.secretKeyPass);
        keyToolBuilder = new KeyToolBuilder(keyConfig, keyProperties);
    }
    
    private void createKeystores() throws Exception {
        deleteKeyStores();
        KeyTool.main(keyToolBuilder.buildKeyToolGenKeyPairArgs(client0Config.keyStore, client0Config.keyAlias, client0Config.cn));
        KeyTool.main(keyToolBuilder.buildKeyToolExportCertArgs(client0Config.keyStore, client0Config.keyAlias, properties.client0Cert));
        KeyTool.main(keyToolBuilder.buildKeyToolGenKeyPairArgs(client1Config.keyStore, client1Config.keyAlias, client1Config.cn));
        KeyTool.main(keyToolBuilder.buildKeyToolExportCertArgs(client1Config.keyStore, client1Config.keyAlias, properties.client1Cert));
        KeyTool.main(keyToolBuilder.buildKeyToolGenKeyPairArgs(client2Config.keyStore, client2Config.keyAlias, client2Config.cn));
        KeyTool.main(keyToolBuilder.buildKeyToolExportCertArgs(client2Config.keyStore, client2Config.keyAlias, properties.client2Cert));
        KeyTool.main(keyToolBuilder.buildKeyToolGenKeyPairArgs(providerConfig.keyStore, providerConfig.keyAlias, providerConfig.cn));
        KeyTool.main(keyToolBuilder.buildKeyToolExportCertArgs(providerConfig.keyStore, providerConfig.keyAlias, properties.providerCert));
        //KeyTool.main(keyToolBuilder.buildKeyToolGenSecKeyArgs(cipherConfig.secretKeyStore, cipherConfig.secretAlias));
        KeyTool.main(keyToolBuilder.buildKeyToolGenKeyPairArgs(cipherConfig.privateKeyStore, cipherConfig.privateAlias, cipherConfig.cn));
        KeyTool.main(keyToolBuilder.buildKeyToolImportCertArgs(cipherConfig.trustKeyStore, properties.providerCertAlias, properties.providerCert));
        KeyTool.main(keyToolBuilder.buildKeyToolImportCertArgs(cipherConfig.trustKeyStore, properties.client0CertAlias, properties.client0Cert));
        KeyTool.main(keyToolBuilder.buildKeyToolImportCertArgs(cipherConfig.trustKeyStore, properties.client1CertAlias, properties.client1Cert));
        KeyTool.main(keyToolBuilder.buildKeyToolImportCertArgs(cipherConfig.trustKeyStore, properties.client2CertAlias, properties.client2Cert));
        KeyTool.main(keyToolBuilder.buildKeyToolExportCertArgs(cipherConfig.privateKeyStore, cipherConfig.privateAlias, properties.cipherCert));
        KeyTool.main(keyToolBuilder.buildKeyToolImportCertArgs(providerConfig.trustStore, providerConfig.trustAlias, properties.cipherCert));
        KeyTool.main(keyToolBuilder.buildKeyToolImportCertArgs(client0Config.trustStore, providerConfig.trustAlias, properties.cipherCert));
        KeyTool.main(keyToolBuilder.buildKeyToolImportCertArgs(client1Config.trustStore, providerConfig.trustAlias, properties.cipherCert));
        KeyTool.main(keyToolBuilder.buildKeyToolImportCertArgs(client2Config.trustStore, providerConfig.trustAlias, properties.cipherCert));
        KeyTool.main(keyToolBuilder.buildKeyToolListArgs(cipherConfig.privateKeyStore));
        KeyTool.main(keyToolBuilder.buildKeyToolListArgs(cipherConfig.trustKeyStore));
        KeyTool.main(keyToolBuilder.buildKeyToolListArgs(providerConfig.keyStore));
        KeyTool.main(keyToolBuilder.buildKeyToolListArgs(providerConfig.trustStore));
        KeyTool.main(keyToolBuilder.buildKeyToolListArgs(client0Config.keyStore));
        KeyTool.main(keyToolBuilder.buildKeyToolListArgs(client0Config.trustStore));
    }
    
    private void deleteKeyStores() {
        new File(client0Config.keyStore).delete();
        new File(client1Config.keyStore).delete();
        new File(client2Config.keyStore).delete();
        new File(client0Config.trustStore).delete();
        new File(client1Config.trustStore).delete();
        new File(client2Config.trustStore).delete();
        new File(providerConfig.keyStore).delete();
        new File(providerConfig.trustStore).delete();
        new File(cipherConfig.privateKeyStore).delete();
        new File(cipherConfig.trustKeyStore).delete();
        new File(cipherConfig.secretKeyStore).delete();
    }
    
    private List<AdminUser> buildUserList() {
        List<AdminUser> userList = new ArrayList();
        AdminUser user0 = new AdminUser(properties.username0, properties.username0, AdminRole.SUPERVISOR, true);
        AdminUser user1 = new AdminUser(properties.username1, properties.username1, AdminRole.OPERATOR, true);
        AdminUser user2 = new AdminUser(properties.username2, properties.username2, AdminRole.OPERATOR, true);
        userList.add(user0);
        userList.add(user1);
        userList.add(user2);
        return userList;
    }
    
    public static void main(String[] args) {
        TestVenigma instance = new TestVenigma();
        try {
            if (true) {
                new Thread(instance).start();
                Thread.sleep(4000);
                Streams.close(instance.server);
            } else {
                instance.process();
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        } finally {
            Streams.close(instance.server);
        }
    }
}
