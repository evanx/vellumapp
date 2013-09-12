/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package venigma.server;

import java.security.*;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

/**
 *
 * @author evan
 */
public class VCipher {
    VCipherSpi spi = new VCipherSpi();
    
    public VCipher() {
    }

    public void init(int opmode, Key key, SecureRandom sr) throws InvalidKeyException, InvalidAlgorithmParameterException {
        spi.engineInit(opmode, key, sr);
    }
    
    public void init(int opmode, Key key, AlgorithmParameterSpec aps, SecureRandom sr) throws InvalidKeyException, InvalidAlgorithmParameterException {
        spi.engineInit(opmode, key, aps, sr);
    }
    
    public void init(int opmode, Key key, AlgorithmParameters ap, SecureRandom sr) throws InvalidKeyException, InvalidAlgorithmParameterException {
        spi.engineInit(opmode, key, ap, sr);
    }
    
    public byte[] doFinal(byte[] input, int inputOffset, int inputLen) throws IllegalBlockSizeException, BadPaddingException {
        return spi.engineDoFinal(input, inputOffset, inputLen);
    }
    
    public byte[] getIV() {
        return spi.engineGetIV();
    }
    
}
