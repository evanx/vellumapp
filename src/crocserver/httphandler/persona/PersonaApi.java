/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package crocserver.httphandler.persona;

import vellum.util.JsonStrings;
import java.net.URL;
import java.net.URLEncoder;
import javax.net.ssl.HttpsURLConnection;
import vellum.logr.Logr;
import vellum.logr.LogrFactory;
import vellum.parameter.StringMap;
import vellum.security.DefaultKeyStores;
import vellum.util.Args;
import vellum.util.Streams;

/**
 *
 * @author evan.summers
 */
public class PersonaApi {

    Logr logger = LogrFactory.getLogger(getClass());
    String serverUrl;

    public PersonaApi(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public PersonaUserInfo getUserInfo(String assertion) throws Exception {
        URL url = new URL("https://verifier.login.persona.org/verify");
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setSSLSocketFactory(DefaultKeyStores.createSSLSocketFactory());
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
        connection.setDoOutput(true);
        StringBuilder builder = new StringBuilder();
        builder.append("assertion=").append(assertion);
        builder.append("&audience=").append(URLEncoder.encode(serverUrl, "UTF-8"));
        logger.info("request", url, builder.toString());
        connection.getOutputStream().write(builder.toString().getBytes());
        String json = Streams.readString(connection.getInputStream());
        logger.info("json", json);
        StringMap map = JsonStrings.getStringMap(json);
        String status = map.get("status");
        if (status != null && status.equals("okay")) {
            return new PersonaUserInfo(map);
        }
        return null;
    }

    @Override
    public String toString() {
        return Args.format(serverUrl);
    }
}
