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
package crocserver.app;

import bizstat.enumtype.ServiceStatus;
import crocserver.storage.servicerecord.ServiceRecord;
import vellum.format.DefaultDateFormats;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import vellum.datatype.IntegerCounterMap;

/**
 *
 * @author evan.summers
 */
public class CrocMessageBuilder {

    List<ServiceRecord> serviceRecords;
    StringBuilder messageBuilder = new StringBuilder();
    IntegerCounterMap<ServiceStatus> counterMap = new IntegerCounterMap();

    public CrocMessageBuilder(LinkedList<ServiceRecord> serviceRecords) {
        this.serviceRecords = serviceRecords;
    }

    public String buildHtml() {
        StringBuilder textBuilder = new StringBuilder();
        textBuilder.append(spanStyle("font-size: 14pt;", buildMessage()));
        for (ServiceRecord serviceRecord : serviceRecords) {
            textBuilder.append("\n<hr>");
            textBuilder.append(spanStyle("font-size: 12pt;", buildHtmlMessage(serviceRecord)));
            textBuilder.append("\n<br>");
            textBuilder.append(serviceRecord.getOutText());
            textBuilder.append("\n");
            if (serviceRecord.getErrText().length() > 0) {
                textBuilder.append(String.format("\n<b>stderr:</b>\n"));
                textBuilder.append(serviceRecord.getErrText());
                textBuilder.append("\n");
            }
        }
        return textBuilder.toString();
    }

    private String spanStyle(String style, String content) {
        return String.format("<span style=\"%s\">%s</span>", style, content);
    }

    public String buildMessage() {
        if (messageBuilder.length() == 0) {
            for (ServiceRecord serviceRecord : serviceRecords) {
                counterMap.increment(serviceRecord.getServiceStatus());
            }
            append(ServiceStatus.CRITICAL);
            append(ServiceStatus.WARNING);
            append(ServiceStatus.OK);
        }
        return messageBuilder.toString();
    }

    private void append(ServiceStatus serviceStatus) {
        if (counterMap.getInt(serviceStatus) > 0) {
            if (messageBuilder.length() > 0) {
                messageBuilder.append("; ");
            }
            messageBuilder.append(String.format("%d %s", counterMap.getInt(serviceStatus), serviceStatus));
            StringBuilder builder = new StringBuilder();
            for (ServiceRecord serviceRecord : serviceRecords) {
                if (serviceRecord.getServiceStatus() == serviceStatus) {
                    if (builder.length() > 0) {
                        builder.append(", ");
                    }
                    builder.append(String.format("%s",
                            serviceRecord.getCertName()));
                }
            }
            messageBuilder.append(" (");
            messageBuilder.append(builder.toString());
            messageBuilder.append(")");
        }
    }

    public static String buildHtmlMessage(ServiceRecord serviceRecord) {
        StringBuilder builder = new StringBuilder();
        builder.append(DefaultDateFormats.timeSecondsFormat.format(new Date(serviceRecord.getTimestamp())));
        builder.append(" ");
        builder.append("<i>");
        builder.append(serviceRecord.getCertName());
        builder.append("</i>");
        builder.append(" ");
        builder.append("<b>");
        builder.append(serviceRecord.getServiceName());
        builder.append("</b>");
        builder.append(" ");
        builder.append(serviceRecord.getServiceStatus().name());
        return builder.toString();
    }

    public static String buildTextMessage(ServiceRecord serviceRecord) {
        StringBuilder builder = new StringBuilder();
        builder.append(DefaultDateFormats.timeSecondsFormat.format(new Date(serviceRecord.getTimestamp())));
        builder.append(" ");
        builder.append(serviceRecord.getCertName());
        builder.append(" ");
        builder.append(serviceRecord.getServiceName());
        builder.append(" ");
        builder.append(serviceRecord.getServiceStatus().name());
        if (serviceRecord.getOutText() != null) {
            String text = serviceRecord.getOutText().trim();
            int index = text.lastIndexOf("\n");
            if (index > 0) {
                builder.append(" - ");
                builder.append(text.substring(index + 1));
            } else {
                builder.append(" - ");
                builder.append(text);
            }
        }
        return builder.toString();
    }
    
    public static String buildOutText(ServiceRecord serviceRecord) {
        if (serviceRecord.getOutText() != null) {
            String text = serviceRecord.getOutText().trim();
            int index = text.lastIndexOf("\n");
            if (index > 0) {
                return text.substring(index + 1);
            } else {
                return text;
            }
        }
        return "";
    }
    
}
