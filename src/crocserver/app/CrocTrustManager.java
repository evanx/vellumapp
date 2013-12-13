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

import crocserver.storage.clientcert.Cert;
import java.security.cert.X509Certificate;
import java.sql.SQLException;
import javax.net.ssl.X509TrustManager;
import vellum.logr.Logr;
import vellum.logr.LogrFactory;
import vellum.security.Certificates;
import vellum.security.DefaultKeyStores;

/**
 *
 * @author evan.summers
 */
public class CrocTrustManager implements X509TrustManager {

    Logr logger = LogrFactory.getLogger(CrocTrustManager.class);
    CrocApp app;
    X509TrustManager trustManager;

    public CrocTrustManager(CrocApp app) {
        this.app = app;
    }

    public void init() throws Exception {
        trustManager = DefaultKeyStores.loadTrustManager();
        for (X509Certificate acceptedIssuer : trustManager.getAcceptedIssuers()) {
            logger.trace("acceptedIssuer", acceptedIssuer.getSubjectDN().getName());
        }
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        logger.info("getAcceptedIssuers", app.getServerCert().getSubjectDN().getName());
        return new X509Certificate[] {app.getServerCert()};
    }

    @Override
    public void checkClientTrusted(X509Certificate[] certs, String authType) {
        String dname = certs[0].getSubjectDN().getName();
        logger.info("checkClientTrusted " + dname);
        logger.info("server", app.getServerCert().getSubjectDN().getName());
        if (dname.equals(app.getServerCert().getSubjectDN().getName())) {
            return;
        }
        try {
            String cname = Certificates.getCommonName(dname);
            Cert cert = app.getStorage().getCertStorage().findName(cname);
            if (cert == null) {
                logger.info("cert not found", cname);
                throw new RuntimeException(dname);
            }
            logger.info("cert", cert.getSubject());
            if (!cert.isEnabled()) {
                throw new RuntimeException(dname);
            }
        } catch (SQLException e) {
            logger.warn(e, dname);
        }
    }

    @Override
    public void checkServerTrusted(X509Certificate[] certs, String authType) {
        logger.info("checkServerTrusted");
    }
}
