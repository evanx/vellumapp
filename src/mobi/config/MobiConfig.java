/*
 */
package mobi.config;

/**
 *
 * @author evan.summers
 */
public class MobiConfig {

    public static String getProperty(String name) {
        return System.getProperty(name);
    }
    
}
