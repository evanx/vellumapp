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
 */
package search.app;

import search.util.JsonConfigParser;
import java.security.KeyStore;
import java.security.SecureRandom;
import javax.net.ssl.SSLContext;
import localca.SSLContexts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.crypto.rsa.RsaKeyStores;
import vellum.httpserver.VellumHttpsServer;

/**
 *
 * @author evan.summers
 */
public class SearchApp {

    Logger logger = LoggerFactory.getLogger(getClass());
    JsonConfigParser config = new JsonConfigParser();
    SearchProperties properties = new SearchProperties();
    SearchStorage storage = new SearchStorage();
    VellumHttpsServer httpsServer;
    
    public void init() throws Exception {
        config.init(properties.getConfFileName());
        properties.init(config.getProperties());
        storage.init();
        httpsServer = new VellumHttpsServer(config.getProperties("httpsServer"));
        char[] keyPassword = Long.toString(new SecureRandom().nextLong() & 
                System.currentTimeMillis()).toCharArray();
        KeyStore keyStore = RsaKeyStores.createKeyStore("JKS", 
                getClass().getSimpleName(), keyPassword, 365);
        SSLContext sslContext = SSLContexts.create(keyStore, keyPassword, 
                new SearchTrustManager(this));
        httpsServer.init(sslContext);        
        logger.info("initialized");
    }

    public void start() throws Exception {
        if (httpsServer != null) {
            httpsServer.start();
            httpsServer.createContext("/", new SearchHttpHandler(this));
            logger.info("HTTPS server started");
        }
        logger.info("started");
    }
    
    public void stop() throws Exception {
        if (httpsServer != null) {
            httpsServer.stop();
        }
    }

    public SearchStorage getStorage() {
        return storage;
    }
    
    public static void main(String[] args) throws Exception {
        try {
            SearchApp app = new SearchApp();
            app.init();
            app.start();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }
}
