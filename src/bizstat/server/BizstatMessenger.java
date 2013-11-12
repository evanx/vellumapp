/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package bizstat.server;

import bizstat.entity.Contact;
import bizstat.entity.ServiceRecord;
import vellum.util.Lists;
import vellum.logr.Logr;
import vellum.logr.LogrFactory;
import vellum.util.Streams;
import java.util.*;
import vellum.config.ConfigProperties;

/**
 *
 * @author evan.summers
 */
public class BizstatMessenger {

    Logr logger = LogrFactory.getLogger(BizstatMessenger.class);
    BizstatServer server;
    Contact contact;
    ProcessBuilder processBuilder = new ProcessBuilder();
    Map environment = processBuilder.environment();
    List<String> argList = new ArrayList();
    LinkedList<ServiceRecord> serviceRecordList;

    public BizstatMessenger(BizstatServer server, Contact contact, List<ServiceRecord> serviceRecordList) {
        this.server = server;
        this.contact = contact;
        this.serviceRecordList = Lists.sortedReverseLinkedList(serviceRecordList, 
                new ServiceRecordStatusComparator());
    }

    public void send() {
        ServiceRecord serviceRecord = serviceRecordList.getFirst();
        logger.info("send", serviceRecordList.size(), serviceRecord);
        String message = BizstatMessageBuilder.buildTextMessage(serviceRecord);
        String text = serviceRecord.getOutText();
        if (serviceRecordList.size() > 1) {
            BizstatMessageBuilder builder = new BizstatMessageBuilder(serviceRecordList);
            message = builder.buildMessage();
            text = builder.buildHtml();
            logger.info("send", text);
        }
        send(serviceRecord, message, text);
        if (contact.getIm() != null) {
            if (server.gtalk != null) {
                try {
                    server.gtalk.sendMessage(contact, message);
                } catch (Exception e) {
                    logger.warn(e, "send", contact);
                }
            }
        }
    }

    private void send(ServiceRecord serviceRecord, String message, String text) {
        logger.info("send", message);
        argList.add(server.config.notifyScript);
        argList.add(serviceRecord.getHost().getName());
        argList.add(serviceRecord.getService().getName());
        argList.add(serviceRecord.getServiceStatus().name());
        argList.add(contact.getEmail());
        logger.info("notify send", argList);
        processBuilder.command(argList);
        environment.put("notify_host", serviceRecord.getHost().getName());
        environment.put("notify_service", serviceRecord.getService().getName());
        environment.put("notify_status", serviceRecord.getServiceStatus().name());
        environment.put("notify_message", message);
        put("notify_", contact.getPropertiesMap());
        send(text);
    }

    private void send(String text) {
        if (server.getConfig().isRun()) {
            try {
                Process process = processBuilder.start();
                process.getOutputStream().write(text.getBytes());
                process.getOutputStream().close();
                int exitCode = process.waitFor();
                String outText = Streams.readString(process.getInputStream());
                String errText = Streams.readString(process.getErrorStream()).trim();
                if (errText.length() > 0) {
                    logger.warn(errText);
                }
                logger.verbose("notify exec", exitCode, outText.length(), outText);
                if (exitCode != 0) {
                    logger.warn("notify stderr", outText);
                } else {
                }
            } catch (Exception e) {
                logger.warn(e, "notify", argList);
            }
        }
    }

    private void put(String prefix, ConfigProperties propertiesMap) {
        for (String key : propertiesMap.keySet()) {
            environment.put(prefix + key, propertiesMap.get(key));
        }
    }
}
