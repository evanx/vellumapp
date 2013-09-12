/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package crocserver.httphandler.facebook;

import java.net.URL;
import java.net.URLEncoder;
import javax.net.ssl.HttpsURLConnection;
import vellum.config.PropertiesStringMap;
import vellum.logr.Logr;
import vellum.logr.LogrFactory;
import vellum.security.DefaultKeyStores;
import vellum.util.Args;
import vellum.util.Streams;

/**
 *
 * @author evan.summers
 */
public class FacebookApi {

    Logr logger = LogrFactory.getLogger(getClass());
    String clientId;
    String serverUrl;
    String redirectUrl;
    String apiKey;

    public FacebookApi(String serverUrl, String redirectUrl, PropertiesStringMap props) {
        this.serverUrl = serverUrl;
        this.redirectUrl = redirectUrl;
        clientId = props.get("clientId");
    }

    public String sendVerify(String assertion) throws Exception {
        URL url = new URL("https://accounts.google.com/o/oauth2/token");
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setSSLSocketFactory(DefaultKeyStores.createSSLSocketFactory());
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
        connection.setDoOutput(true);
        StringBuilder builder = new StringBuilder();
        builder.append("assertion=").append(assertion);
        builder.append("&audience=").append(URLEncoder.encode(redirectUrl, "UTF-8"));
        logger.info("request", url, builder.toString());
        connection.getOutputStream().write(builder.toString().getBytes());
        String responseText = Streams.readString(connection.getInputStream());
        logger.info("response", responseText);
        return responseText;
    }

    public String getClientId() {
        return clientId;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    @Override
    public String toString() {
        return Args.format(clientId);
    }
}
