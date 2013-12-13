/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package venigma.server;

import venigma.server.storage.DatabaseConnectionInfo;

/**
 *
 * @author evan
 */
public class CipherConfig {
    public String serverIp = "localhost";
    public String privateAlias = "cipher.private";
    public String cn = "cipher";
    public int sslPort = 7443;
    public int backlog = 4;
    public String privateKeyStore; 
    public String trustKeyStore; 
    public String secretKeyStore; 
    public String secretAlias = "cipher.secret";
    
    public DatabaseConnectionInfo databaseConnectionInfo = new DatabaseConnectionInfo(
            "org.h2.Driver",
            "jdbc:h2:tcp://localhost/~/cipher",
            "sa"            
            );

}
