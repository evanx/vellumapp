/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package banta;

import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import org.junit.Test;
import vellum.util.Streams;
import vellumexp.logr.Logr;
import vellumexp.logr.LogrFactory;

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
        String json = Streams.readResourceString(getClass(), "login.json");
        System.out.println(json);
        stream.write(json.getBytes());
        String response = Streams.readString(connection.getInputStream());
        System.out.println(response);
    }
}
