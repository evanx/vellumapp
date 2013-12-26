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

import vellumexp.json.JsonStrings;
import vellumexp.logr.Logr;
import vellumexp.logr.LogrFactory;
import vellum.util.Args;

/**
 *
 * @author evan.summers
 */
public class GooglePlusUserInfo {
    static Logr logger = LogrFactory.getLogger(GooglePlusUserInfo.class);
    String json;
    String email;
    String displayName;
    String givenName;
    String familyName;
    
    public GooglePlusUserInfo() {
    }

    public GooglePlusUserInfo(String json) {
        this.json = json;
    }
    
    public GooglePlusUserInfo(String email, String displayName) {
        this.email = email;
        this.displayName = displayName;
    }

    public void parseJson(String json) {
        this.json = json;
        logger.info(json);
        setEmail(JsonStrings.get(json, "email"));
        setDisplayName(JsonStrings.get(json, "name"));
        setGivenName(JsonStrings.get(json, "given_name"));
        setFamilyName(JsonStrings.get(json, "family_name"));
        
    }
    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getGivenName() {
        return givenName;
    }
        
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public String getJson() {
        return json;
    }

    
    @Override
    public String toString() {
        return Args.format(email, displayName);
    }

    
}
