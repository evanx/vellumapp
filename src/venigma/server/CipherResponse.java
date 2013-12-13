/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package venigma.server;

import vellum.util.Args;
import vellum.util.Bytes;

/**
 *
 * @author evan
 */
public class CipherResponse {
    CipherResponseType responseType;
    String errorMessage;
    byte[] bytes;
    byte[] iv;

    public CipherResponse(CipherResponseType responseType, byte[] bytes) {
        this.responseType = responseType;
        this.bytes = bytes;
    }

    public CipherResponse(CipherResponseType responseType, byte[] bytes, byte[] iv) {
        this.responseType = responseType;
        this.bytes = bytes;
        this.iv = iv;
    }
    
    public CipherResponse(CipherResponseType responseType) {
        this.responseType = responseType;
    }

    public CipherResponse(Throwable throwable) {
        this.responseType = CipherResponseType.ERROR;
        this.errorMessage = throwable.getMessage();
    }
    
    public CipherResponse(String errorMessage) {
        this.responseType = CipherResponseType.ERROR;
        this.errorMessage = errorMessage;
    }
    
    public byte[] getBytes() {
        return bytes;
    }

    public byte[] getIv() {
        return iv;
    }

    public void setIv(byte[] iv) {
        this.iv = iv;
    }
        
    public CipherResponseType getResponseType() {
        return responseType;
    }
    
    @Override
    public String toString() {
        return Args.format(responseType.name(), errorMessage, Bytes.formatHex(bytes), Bytes.formatHex(iv));
    }   
}
