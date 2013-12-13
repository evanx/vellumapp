/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package saltserver.app;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author evan.summers
 */
public class VaultPasswordManager {
    private Map<String, char[]> passwordMap = new HashMap();

    public Map<String, char[]> getPasswordMap() {
        return passwordMap;
    }

    public int getPasswordMapSize() {
        return passwordMap.size();
    }
    
    public void clear() {
        for (String key : passwordMap.keySet()) {
            char[] value = passwordMap.get(key);
            Arrays.fill(value, '0');
        }
        passwordMap.clear();
    }

    public void put(String principalName, char[] toCharArray) {
        passwordMap.put(principalName, toCharArray);
        
    }
}
