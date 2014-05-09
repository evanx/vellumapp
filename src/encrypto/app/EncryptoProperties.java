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

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.exception.ParseException;
import vellum.json.JsonObjectDelegate;
import vellum.jx.JMapException;
import vellum.mail.MailerProperties;
import vellum.util.ExtendedProperties;
import vellum.util.Streams;

/**
 *
 * @author evan.summers
 */
public class EncryptoProperties {

    static Logger logger = LoggerFactory.getLogger(EncryptoProperties.class);

    String siteUrl;
    boolean testing = false;
    Set<String> adminEmails = new HashSet();
    ExtendedProperties webServer;
    final ExtendedProperties properties = new ExtendedProperties(System.getProperties());
    final MailerProperties mailerProperties = new MailerProperties();

    public void init() throws IOException, ParseException, JMapException {
        String jsonConfigFileName = properties.getString("config.json", "config.json");
        JsonObjectDelegate object = new JsonObjectDelegate(new File(jsonConfigFileName));
        siteUrl = object.getString("siteUrl");
        testing = object.getBoolean("testing", testing);
        adminEmails = object.getStringSet("adminEmails");
        webServer = object.getProperties("webServer");
        mailerProperties.init(object.getProperties("mailer"));
        mailerProperties.setLogoBytes(Streams.readBytes(getClass().getResourceAsStream("/resources/app48.png")));
        logger.info("mailer {}", mailerProperties);
    }

    public boolean isTesting() {
        return testing;
    }

    public MailerProperties getMailerProperties() {
        return mailerProperties;
    }

    public ExtendedProperties getWebServer() {
        return webServer;
    }       
}
