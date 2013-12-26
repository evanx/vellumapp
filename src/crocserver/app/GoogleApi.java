/*
 * Source https://github.com/evanx by @evanxsummers

       Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements. See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership.  The ASF licenses this file
       to you under the Apache License, Version 2.0 (the
       "License"); you may not use this file except in compliance
       with the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.  
 */
package crocserver.app;

import vellum.util.JsonStrings;
import java.net.URL;
import java.net.URLEncoder;
import javax.net.ssl.HttpsURLConnection;
import vellum.config.ConfigProperties;
import vellum.exception.EnumException;
import vellumexp.logr.Logr;
import vellumexp.logr.LogrFactory;
import vellum.security.DefaultKeyStores;
import vellum.util.Args;
import vellum.util.Streams;
import vellum.util.Strings;

/**
 *
 * @author evan.summers
 */
public class GoogleApi {

    Logr logger = LogrFactory.getLogger(getClass());
    String clientId;
    String clientSecret = System.getProperty("google.clientSecret");
    String serverUrl;
    String loginUrl;
    String redirectUrl;
    String apiKey;

    public GoogleApi(String serverUrl, String redirectUrl, ConfigProperties props) {
        this.serverUrl = serverUrl;
        this.redirectUrl = redirectUrl;
        clientId = props.getString("clientId");
    }

    public void init() throws Exception {
        loginUrl = buildLoginUrl();
    }

    public String buildLoginUrl() {
        StringBuilder builder = new StringBuilder();
        builder.append("https://accounts.google.com/o/oauth2/auth");
        builder.append("?state=none");
        builder.append("&response_type=token");
        builder.append("&approval_prompt=force");
        builder.append("&client_id=").append(clientId);
        builder.append("&redirect_uri=").append(Strings.encodeUrl(redirectUrl));
        builder.append("&scope=").append(Strings.encodeUrl("https://www.googleapis.com/auth/userinfo.email"));
        builder.append("+").append(Strings.encodeUrl("https://www.googleapis.com/auth/userinfo.profile"));
        return builder.toString();
    }

    public GoogleUserInfo sendTokenRequest(String code) throws Exception {
        URL url = new URL("https://accounts.google.com/o/oauth2/token");
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setSSLSocketFactory(DefaultKeyStores.createSSLSocketFactory());
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
        connection.setDoOutput(true);
        StringBuilder builder = new StringBuilder();
        builder.append("grant_type=authorization_code");
        builder.append("&client_id=").append(clientId);
        builder.append("&redirect_uri=").append(URLEncoder.encode(redirectUrl, "UTF-8"));
        builder.append("&client_secret=").append(clientSecret);
        builder.append("&code=").append(URLEncoder.encode(code, "UTF-8"));
        logger.info("request", url, builder.toString());
        connection.getOutputStream().write(builder.toString().getBytes());
        String responseText = Streams.readString(connection.getInputStream());
        String accessToken = JsonStrings.get(responseText, "access_token");
        logger.info("response", responseText);
        logger.info("accessToken", accessToken);
        return getUserInfo(accessToken);
    }

    public GoogleUserInfo getUserInfo(String accessToken) throws Exception {
        logger.info("getUserInfo", accessToken);
        String json = getUserInfoJson(accessToken);
        if (JsonStrings.get(json, "email") != null) {
            GoogleUserInfo userInfo = new GoogleUserInfo();
            userInfo.parseJson(json);
            return userInfo;
        }
        logger.warn("getUserInfo", json);
        throw new EnumException(CrocExceptionType.NO_AUTH);
    }

    public String getUserInfoJson(String accessToken) throws Exception {
        URL url = new URL("https://www.googleapis.com/oauth2/v1/userinfo?access_token=" + accessToken);
        logger.info("request", url.toString());
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        String res = Streams.readString(connection.getInputStream());
        logger.info("response", connection.getResponseCode(), res);
        return res;
    }

    public GooglePlusUserInfo getPlusPerson(String accessToken, String userId) throws Exception {
        String json = getPlusPersonJson(accessToken, userId);
        if (JsonStrings.get(json, "email") != null) {
            GooglePlusUserInfo userInfo = new GooglePlusUserInfo();
            userInfo.parseJson(json);
            return userInfo;
        }
        logger.warn("getPlusPerson", json);
        throw new EnumException(CrocExceptionType.NO_AUTH);
    }

    public String getPlusPersonJson(String accessToken, String userId) throws Exception {
        URL url = new URL("https://www.googleapis.com/plus/v1/people/" + userId + "?access_token=" + accessToken);
        logger.info("request", url.toString());
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        return Streams.readString(connection.getInputStream());
    }

    public String getClientId() {
        return clientId;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public String getLoginUrl() {
        return loginUrl;
    }

    @Override
    public String toString() {
        return Args.format(clientId);
    }
}
