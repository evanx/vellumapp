/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package venigma.server;

import java.util.ArrayList;
import java.util.List;
import venigma.data.AdminUser;

/**
 *
 * @author evan
 */
public class CipherProperties {
    public char[] databaseStorePassword;
    public char[] databaseUserPassword;
    public char[] keyStorePassword;
    public char[] privateKeyPassword;
    public char[] trustKeyStorePassword;
    public char[] secretKeyStorePassword;
    public char[] secretKeyPassword;
    public byte[] secretKeyPasswordSalt;
    public byte[] secretKeyIv;
    
    public List<AdminUser> userList = new ArrayList();
    
    public String buildDatabasePassword() {
        return null;
    }
}
