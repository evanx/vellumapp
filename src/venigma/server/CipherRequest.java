/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package venigma.server;

import venigma.data.AdminRole;
import venigma.data.AdminUser;
import venigma.data.KeyInfo;

/**
 *
 * @author evan
 */
public class CipherRequest {
    CipherRequestType requestType;
    byte[] bytes;
    byte[] iv;
    String keyAlias;
    int keyRevisionNumber;
    int keySize;
    String username;
    AdminRole role;
    AdminUser user;
    char[] password;
    
    public CipherRequest(CipherRequestType requestType) {
        this.requestType = requestType;
    }
    
    public CipherRequest(CipherRequestType requestType, String keyAlias, byte[] bytes) {
        this.requestType = requestType;
        this.keyAlias = keyAlias;
        this.bytes = bytes;
    }

    public CipherRequest(CipherRequestType requestType, String keyAlias, byte[] bytes, byte[] iv) {
        this.requestType = requestType;
        this.keyAlias = keyAlias;
        this.bytes = bytes;
        this.iv = iv;
    }

    public KeyInfo getKeyInfo() {
        return new KeyInfo(keyAlias, keyRevisionNumber, keySize);
        
    }
    
    public void setKeyAlias(String keyAlias) {
        this.keyAlias = keyAlias;
    }
    
    public byte[] getBytes() {
        return bytes;
    }

    public byte[] getIv() {
        return iv;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getUsername() {
        return username;
    }

    public void setUser(AdminUser user) {
        this.user = user;
    }
    
    public AdminUser getUser() {
        return user;
    }

    public void setRole(AdminRole role) {
        this.role = role;
    }

    public AdminRole getRole() {
        return role;
    }

    public void setKeySize(int keySize) {
        this.keySize = keySize;
    }
    
    public int getKeySize() {
        return keySize;
    }

    public int getKeyRevisionNumber() {
        return keyRevisionNumber;
    }

    public String getKeyAlias() {
        return keyAlias;
    }

    public void setPassword(char[] password) {
        this.password = password;
    }

    public char[] getPassword() {
        return password;
    }
        
    @Override
    public String toString() {
        return requestType.name();
    }

}
