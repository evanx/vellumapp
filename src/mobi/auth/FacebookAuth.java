/*
 */
package mobi.auth;

import mobi.config.MobiConfig;

/**
 *
 * @author evan.summers
 */
public class FacebookAuth {
    private static final String fbAppId = MobiConfig.getProperty("fbAppId");
    private static final String fbAppSecret = MobiConfig.getProperty("fbAppSecret");
    private static final String clientId = MobiConfig.getProperty("fbClientId");
    private static final String redirectUri = "http://localhost/fbauth";
    private static final String permissions = "publish_stream,email";

    public static String getAPIKey() {
        return fbAppId;
    }

    public static String getSecret() {
        return fbAppSecret;
    }

    public static String getLoginRedirectURL() {
        return "https://graph.facebook.com/oauth/authorize?client_id=" +
            clientId + "&display=page&redirect_uri=" +
            redirectUri+"&scope=" + permissions;
    }

    public static String getAuthURL(String authCode) {
        return "https://graph.facebook.com/oauth/access_token?client_id=" +
            clientId+"&redirect_uri=" +
            redirectUri+"&client_secret="+fbAppSecret+"&code="+authCode;
    }
}
