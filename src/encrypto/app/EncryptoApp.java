/*
 * Source https://github.com/evanx by @evanxsummers

 Licensed to the Apache Software Foundation (ASF) under one
 or more contributor license agreements. See the NOTICE file
 distributed with this work for additional information
 regarding copyright ownership. The ASF licenses this file to
 you under the Apache License, Version 2.0 (the "License").
 You may not use this file except in compliance with the
 License. You may obtain a copy of the License at:

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing,
 software distributed under the License is distributed on an
 "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 KIND, either express or implied.  See the License for the
 specific language governing permissions and limitations
 under the License.  
 */
package encrypto.app;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.data.Millis;
import vellum.exception.ParseException;
import vellum.httpserver.VellumHttpServer;
import vellum.httpserver.VellumHttpsServer;
import vellum.jx.JMap;
import vellum.jx.JMapException;
import vellum.mail.Mailer;
import vellum.ssl.OpenTrustManager;


/**
 *
 * @author evan.summers
 */
public class EncryptoApp {

    Logger logger = LoggerFactory.getLogger(EncryptoApp.class);
    EncryptoProperties properties = new EncryptoProperties();
    Mailer mailer;
    VellumHttpsServer webServer = new VellumHttpsServer();
    VellumHttpServer httpRedirectServer = new VellumHttpServer();
    VellumHttpServer insecureServer = new VellumHttpServer();
    EntityManagerFactory emf;
    boolean initalized = false;
    boolean running = true;
    Thread initThread = new InitThread();
    Thread messageThread = new MessageThread(this);
    ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    public EncryptoApp() {
        super();
    }

    public void initProperties() throws IOException, ParseException, JMapException {
        properties.init();
    }

    public void init() throws Exception {
        properties.init();
        mailer = new Mailer(properties.getMailerProperties());
        logger.info("properties {}", properties);
        webServer.start(properties.getWebServer(),
                new OpenTrustManager(),
                new WebHttpService(this));
        initThread.start();
    }

    public void ensureInitialized() throws InterruptedException {
        logger.info("ensureInitialized");
        if (initThread.isAlive()) {
            initThread.join();
        }
        logger.info("ensureInitialized complete");
    }

    public void initDeferred() throws Exception {
        emf = Persistence.createEntityManagerFactory("encryptoPU");;
        initalized = true;
        logger.info("initialized");
        messageThread.start();
        logger.info("started");
    }

    class InitThread extends Thread {

        @Override
        public void run() {
            try {
                initDeferred();
            } catch (Exception e) {
                logger.warn("init", e);
            }
        }
    }

    public void shutdown() throws Exception {
        logger.info("shutdown");
        running = false;
        executorService.shutdown();
        if (webServer != null) {
            webServer.shutdown();
        }
        if (httpRedirectServer != null) {
            httpRedirectServer.shutdown();
        }
        if (messageThread != null) {
            messageThread.interrupt();
            messageThread.join(2000);
        }
        logger.info("shutdown complete");
    }

    class MessageThread extends Thread {

        EncryptoApp app;

        public MessageThread(EncryptoApp app) {
            this.app = app;
        }

        @Override
        public void run() {
            while (running) {
                try {
                } catch (Throwable t) {
                    logger.warn("run", t);
                }
            }
        }
    }

    public EntityManager createEntityManager() {
        return emf.createEntityManager();
    }

    public void persistEntity(Object entity) {
        EncryptoEntityService es = new EncryptoEntityService(this);
        try {
            es.begin();
            es.persist(entity);
            es.commit();
        } catch (PersistenceException e) {
            logger.warn("persist {} {}", entity, e);
        } finally {
            es.close();
        }
    }

    public EncryptoProperties getProperties() {
        return properties;
    }

    public Mailer getMailer() {
        return mailer;
    }

}
