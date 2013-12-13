/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package venigma.server;

import venigma.provider.CipherConnection;
import java.io.IOException;
import java.security.*;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import vellum.logr.Logr;
import vellum.logr.LogrFactory;
import venigma.provider.VProvider;

/**
 *
 * @author evan
 */
public final class VCipherSpi extends javax.crypto.CipherSpi {
    
    Logr logger = LogrFactory.getLogger(getClass());
    CipherConnection connection = new CipherConnection(VProvider.providerContext);
    String keyAlias = VProvider.providerContext.getKeyAlias();
    int opmode;
    byte[] iv = new byte[16];

    
    public VCipherSpi() {
        super();
    }

    private void init(int opmode, IvParameterSpec ips) {
        iv = ips.getIV();
        init(opmode);
    }

    public void init(int opmode, SecureRandom sr) {
        sr.nextBytes(iv);
        init(opmode);
    }
    
    private void init(int opmode) {
        this.opmode = opmode;
        try {
            connection.open();
        } catch (IOException e) {
            logger.warn(e, null);
        }
    }
    
    @Override
    protected void engineSetMode(String mode) throws NoSuchAlgorithmException {
    }

    @Override
    protected void engineSetPadding(String padding) throws NoSuchPaddingException {
    }

    @Override
    protected int engineGetBlockSize() {
        return 0;
    }

    @Override
    protected int engineGetOutputSize(int outputSize) {
        return 0;
    }

    @Override
    protected byte[] engineGetIV() {
        return iv;
    }

    @Override
    protected AlgorithmParameters engineGetParameters() {
        return null;
    }

    @Override
    protected void engineInit(int opmode, Key key, SecureRandom sr) throws InvalidKeyException {
        init(opmode, sr);
    }

    @Override
    protected void engineInit(int opmode, Key key, AlgorithmParameterSpec aps, SecureRandom sr) throws InvalidKeyException, InvalidAlgorithmParameterException {
        if (aps instanceof IvParameterSpec) {
            init(opmode, (IvParameterSpec) aps);
        } else if (aps instanceof IvParameterSpec) {
            init(opmode, sr);
        } else {
            throw new RuntimeException(CipherResources.ERROR_MESSAGE_NO_IV_SPEC);
        }
    }

    @Override
    protected void engineInit(int opmode, Key key, AlgorithmParameters ap, SecureRandom sr) throws InvalidKeyException, InvalidAlgorithmParameterException {
        try {
            AlgorithmParameterSpec aps = ap.getParameterSpec(IvParameterSpec.class);
            if (aps instanceof IvParameterSpec) {
                init(opmode, (IvParameterSpec) aps);
            } else {
                init(opmode, sr);
            }
        } catch (InvalidParameterSpecException e) {
            throw new RuntimeException(CipherResources.ERROR_MESSAGE_NO_IV_SPEC, e);

        }
    }

    @Override
    protected byte[] engineUpdate(byte[] input, int inputOffset, int inputLen) {
        return null;
    }

    @Override
    protected int engineUpdate(byte[] input, int inputOffset, int inputLen, byte[] output, int outputOffset) throws ShortBufferException {
        return 0;
    }

    @Override
    protected byte[] engineDoFinal(byte[] input, int inputOffset, int inputLen) throws IllegalBlockSizeException, BadPaddingException {
        try {
            if (opmode == Cipher.ENCRYPT_MODE) {
                return encrypt(input);
            } else if (opmode == Cipher.DECRYPT_MODE) {
                return decrypt(input);
            } else {
                throw new RuntimeException(CipherResources.ERROR_MESSAGE_NO_MODE);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] encrypt(byte[] input) throws IOException {
        if (iv == null) {
            throw new RuntimeException(CipherResources.ERROR_MESSAGE_NO_IV_SPEC);
        }
        CipherRequest request = new CipherRequest(CipherRequestType.ENCIPHER, keyAlias, input, iv);
        CipherResponse response = connection.sendCipherRequest(request);
        if (response.responseType != CipherResponseType.OK) {
            throw new CipherResponseRuntimeException(response);
        }
        iv = response.getIv();
        return response.getBytes();
    }

    private byte[] decrypt(byte[] input) throws IOException {
        if (iv == null) {
            throw new RuntimeException(CipherResources.ERROR_MESSAGE_NO_IV_SPEC);
        }
        CipherRequest request = new CipherRequest(CipherRequestType.DECIPHER, keyAlias, input, iv);
        CipherResponse response = connection.sendCipherRequest(request);
        if (response.responseType != CipherResponseType.OK) {
            throw new CipherResponseRuntimeException(response);
        }
        return response.getBytes();
    }
    
    @Override
    protected int engineDoFinal(byte[] input, int inputOffset, int inputLen, byte[] output, int outputOffset) throws ShortBufferException, IllegalBlockSizeException, BadPaddingException {
        return 0;
    }
}
