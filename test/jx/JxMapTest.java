/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package jx;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import jx.JxMap;
import jx.JxMaps;
import org.junit.Test;
import vellum.logr.Logr;
import vellum.logr.LogrFactory;

/**
 *
 * @author evan
 */
public class JxMapTest {

    static Logr logger = LogrFactory.getLogger(JxMapTest.class);

    @Test
    public void test() throws Exception {
        JxMap loginRes = new JxMap();
        loginRes.put("enabled", false);
        loginRes.put("id", 1001);
        loginRes.put("name", "Evan Summers");
        loginRes.put("email", "evanx@gmail.com");
        loginRes.put("contacts", buildContactList());
        System.out.println(loginRes);
        for (Map.Entry entry: JxMaps.parse(loginRes.toString()).entrySet()) {
            System.out.printf("%s = (%s) %s\n", entry.getKey(), entry.getValue().getClass(), entry.getValue());
            if (entry.getValue() instanceof List) {
                for (Object value : (List) entry.getValue()) {
                    System.out.printf("  (%s) %s\n", value.getClass(), value);            
                }
            }
        }
    }
    
    private List buildContactList() {
        List list = new ArrayList();
        list.add(buildContact("Joe Soap", "0724443333", "joes@gmail.com"));
        list.add(buildContact("Harry Potter", "0723335555", "harryp@gmail.com"));
        list.add(buildContact("Ginger Bread", "0224443333", "ginger@gmail.com"));
        return list;
    }
    
    private JxMap buildContact(String name, String phone, String email) {
        JxMap map = new JxMap();
        map.put("enabled", true);
        map.put("name", name);
        map.put("phone", phone);
        map.put("email", email);
        map.put("id", 1001);
        return map;
    }
    
}
