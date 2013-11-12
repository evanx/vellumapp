/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package bizstat.server;

import bizstat.entity.Contact;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import vellum.config.ConfigProperties;
import vellum.logr.Logr;
import vellum.logr.LogrFactory;

/**
 *
 * @author evan.summers
 */
public class BizstatGtalkConnection implements MessageListener {
    Logr logger = LogrFactory.getLogger(BizstatGtalkConnection.class);
    BizstatServer server;
    BizstatConfig config;
    ConfigProperties properties;
        
    String username;
    String password;
    
    XMPPConnection connection;    
    
    public BizstatGtalkConnection(BizstatServer server) {
        this.server = server;
        this.config = server.getConfig();
        properties = server.getConfigMap().get("Gtalk", "default").getProperties();
        username = properties.getString("gtalkUsername", System.getProperty("gtalk.username"));
        password = properties.getString("gtalkPassword", System.getProperty("gtalk.password"));
        logger.info("gtalkUsername", username);
    }

    public void connect() {
        ConnectionConfiguration connectionConfig = new ConnectionConfiguration("talk.google.com", 5222, "gmail.com");
        connectionConfig.setSecurityMode(ConnectionConfiguration.SecurityMode.required);
        connection = new XMPPConnection(connectionConfig);
        try {
            connection.connect();
            connection.login(username, password);
            Presence presence = new Presence(Presence.Type.available);
            connection.sendPacket(presence);
        } catch (Exception e) {
            logger.warn(e, null);
            e.printStackTrace(System.err);
        }  
    }

    public void sendMessage(Contact contact, String message) throws Exception {
        String im = contact.getIm();
        logger.info("sendMessage", contact);
        ChatManager chatManager = connection.getChatManager();
        Chat chat = chatManager.createChat(im, this);
        Message msg = new Message(im, Message.Type.chat);
        msg.setBody(message);
        chat.sendMessage(msg);
    }
    
    @Override
    public void processMessage(Chat chat, Message message) {
        logger.info("processMesssage", message.getClass(), message.getType(), message.getError());
        if (message.getType().equals(Message.Type.chat) && message.getBody() != null) {
            logger.info(message.getBody());
            try {
                Message reply = new Message(chat.getParticipant(), Message.Type.chat);
                reply.setBody("ack: " + message.getBody());
                chat.sendMessage(reply);
            } catch (XMPPException e) {
                logger.warn(e, null);
            }
        } else {
            logger.info("processMesssage", message.getType());
        }
    }

    public void close() {
        connection.disconnect();
    }
}
