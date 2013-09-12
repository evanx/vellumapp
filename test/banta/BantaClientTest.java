/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package banta;

import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import org.junit.Test;
import vellum.logr.Logr;
import vellum.logr.LogrFactory;
import vellum.util.Streams;

/**
 *
 * @author evan
 */
public class BantaClientTest {

    static Logr logger = LogrFactory.getLogger(BantaClientTest.class);

    @Test
    public void test() throws Exception {
        URLConnection connection = new URL("http://localhost:8080/login").openConnection();
        connection.setDoOutput(true);
        OutputStream stream = connection.getOutputStream();
        String json = readResourceString("login.json");
        System.out.println(json);
        stream.write(json.getBytes());
        String response = Streams.readString(connection.getInputStream());
        System.out.println(response);
    }
    
    private String readResourceString(String resourceName) {
        return Streams.readResourceString(BantaClientTest.class, resourceName);
    }
    
}
