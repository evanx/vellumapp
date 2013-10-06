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
package crocserver.notify;

import bizstat.enumtype.NotifyType;
import bizstat.enumtype.ServiceStatus;
import crocserver.app.CrocApp;
import crocserver.storage.servicerecord.ServiceRecord;
import java.text.MessageFormat;
import vellum.logr.Logr;
import vellum.logr.LogrFactory;
import vellum.util.Strings;

/**
 *
 * @author evan.summers
 */
public class ServiceRecordProcessor {
    Logr logger = LogrFactory.getLogger(getClass());

    CrocApp app;
    NotifyType notifyType;
    ServiceRecord previousRecord; 
    ServiceRecord newRecord;
    boolean notify = false;
    
    public ServiceRecordProcessor(CrocApp app) {
        this.app = app;
    }

    public void process(NotifyType notifyType, ServiceRecord previousRecord, ServiceRecord newRecord) {
        this.notifyType = notifyType;
        this.previousRecord = previousRecord;
        this.newRecord = newRecord;
        processNotifyType();
    }

    public void processNotifyType() {
        if (notifyType == NotifyType.ALWAYS) {
            notify = true;
        } else if (notifyType == NotifyType.OUTPUT_CHANGED) {
            if (previousRecord == null) {
                notify = false;
            } else if (!Strings.equals(previousRecord.getOutText(), newRecord.getOutText())) {
                notify = true;
                logger.info("output changed", previousRecord.getOutText().length(), newRecord.getOutText().length());
            }
        } else if (notifyType == NotifyType.NOT_OK) {
            if (newRecord.getServiceStatus() == ServiceStatus.WARNING || 
                    newRecord.getServiceStatus() == ServiceStatus.CRITICAL) {
                notify = true;
            }
        } else if (notifyType == NotifyType.STATUS_CHANGED) {
            if (previousRecord == null) {
                notify = false;
            } else if (previousRecord.getServiceStatus().isKnown() &&
                    newRecord.getServiceStatus().isKnown() &&
                    previousRecord.getServiceStatus() != newRecord.getServiceStatus()) {
                notify = true;
            }
        }
        if (notify) {
            newRecord.setNotify(notify);
            app.sendAdminGtalkMessage(MessageFormat.format("CHANGED @{0} {1} {2}/view/serviceRecord/{3}",
                    newRecord.getCertName(), newRecord.getServiceName(), app.getSecureUrl(), newRecord.getId()));
        }
    }

    public boolean isNotify() {
        return notify;
    }  
    
}
