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

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import search.util.JsonConfigParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import search.util.EphemeralSSLContexts;
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
        httpsServer.init(new EphemeralSSLContexts().create(getClass().getSimpleName()));
        logger.info("initialized");
    }

    public void start() throws Exception {
        if (httpsServer != null) {
            httpsServer.start();
            httpsServer.createContext("/", new SearchHttpHandler(this));
            httpsServer.createContext("/stop", new HttpHandler() {

                @Override
                public void handle(HttpExchange he) throws IOException {
                    if (he.getRemoteAddress().getAddress().equals(
                            InetAddress.getLocalHost())) {
                        shutdown();
                        he.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                    } else {
                        he.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, 0);
                    }
                    he.close();
                }
            });
            logger.info("HTTPS server started");
        }
        logger.info("started");
    }
    
    public void shutdown() throws IOException {
        if (httpsServer != null) {
            httpsServer.shutdown();
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
