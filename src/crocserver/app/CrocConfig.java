/*
 * Source https://code.google.com/p/vellum by @evanxsummers

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
      
 * -Djavax.net.ssl.keyStore=security/croc.jks
 * -Djavax.net.ssl.keyStorePassword=crocserver
 * -Djavax.net.ssl.keyPassword=crocserver
 * -Djavax.net.ssl.trustStore=security/croc.jks
 * -Djavax.net.ssl.trustStorePassword=crocserver
 */
package crocserver.app;

import vellum.util.SystemProperties;

/**
 * 
 * @author evan.summers
 */
public class CrocConfig {
    boolean testing = SystemProperties.getBoolean(
            "croc.testing");    
    String confFileName = SystemProperties.getString(
            "croc.conf", "conf/croc.conf");
    String serverKeyAlias = SystemProperties.getString(
            "serverKeyAlias", "crocserver");
    String keyStoreLocation = SystemProperties.getString(
            "javax.net.ssl.keyStore", "security/croc.jks");
    char[] keyStorePassword = SystemProperties.getString(
            "javax.net.ssl.keyStorePassword", "crocserver").toCharArray();
    char[] keyPassword = SystemProperties.getString(
            "javax.net.ssl.keyPassword", "crocserver").toCharArray();
    String trustStoreLocation = SystemProperties.getString(
            "javax.net.ssl.trustStore", "security/croc.jks");
    char[] trustStorePassword = SystemProperties.getString(
            "javax.net.ssl.trustStorePassword", "crocserver").toCharArray();

    public boolean isTesting() {
        return testing;
    }        
}
