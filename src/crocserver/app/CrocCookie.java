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

import java.util.Map;
import vellum.data.Millis;
import vellum.exception.EnumException;
import vellum.jx.JMap;
import vellum.parameter.StringMap;

/**
 *
 * @author evan.summers
 */
public class CrocCookie {
    public static final long MAX_AGE_MILLIS = Millis.fromHours(16);
    
    String email;
    String displayName;
    String accessToken;
    long loginMillis;
    String authCode; 
            
    public CrocCookie() {
    }

    public CrocCookie(Map map) {
        this(new StringMap(map));
    }
    
    public CrocCookie(StringMap map) {
        email = map.get(CrocCookieMeta.email.name());
        displayName = map.get(CrocCookieMeta.displayName.name());
        accessToken = map.get(CrocCookieMeta.accessToken.name());
        loginMillis = map.getLong(CrocCookieMeta.loginMillis.name(), 0);
        authCode = map.get(CrocCookieMeta.authCode.name());
    }
    
    public CrocCookie(String email, String displayName, long loginMillis, String accessToken) {
        this.email = email;
        this.displayName = displayName;
        this.loginMillis = loginMillis;
        this.accessToken = accessToken;
    }

    public String getEmail() {
        return email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public JMap toMap() {
        JMap map = new JMap();
        map.put(CrocCookieMeta.email.name(), email);
        map.put(CrocCookieMeta.displayName.name(), displayName);
        map.put(CrocCookieMeta.loginMillis.name(), Long.toString(loginMillis));
        map.put(CrocCookieMeta.authCode.name(), authCode);
        map.put(CrocCookieMeta.accessToken.name(), accessToken);
        return map;
    }

    public void createAuthCode(byte[] secret) throws Exception {
        authCode = CrocSecurity.createCode(secret, email, loginMillis);
    }

    public String getAuthCode() {
        return authCode;
    }
    
    public void validateAuthCode(byte[] secret) throws Exception {
        String code = CrocSecurity.createCode(secret, email, loginMillis);
        if (!authCode.equals(code)) {
            throw new EnumException(CrocExceptionType.INVALID_COOKIE);
        }
    }
    
    public long getLoginMillis() {
        return loginMillis;
    }

    public String getAccessToken() {
        return accessToken;
    }
    
    @Override
    public String toString() {
        return toMap().toString();
    }
}
