/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package bizstat.mailer;

import java.util.Date;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

/**
 *
 * @author evan.summers
 */
public class Mailer {

    String logoFileName;
    String logoResourceName;
    String organisation;
    String from;
    String username;
    String password;
    String host = "localhost";
    int port = 25;
    String localhost;
    
    public void sendEmail(String recipient, String subject, String htmlContent) throws Exception {
        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.localhost", localhost);
        props.put("mail.smtp.auth", "true");
        Authenticator auth = new Authenticator() {
            @Override
            public PasswordAuthentication getPasswordAuthentication() {
               return new PasswordAuthentication(username, password);
            }
        };
        Session session = Session.getInstance(props, auth);
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        message.setSentDate(new Date());
        message.setSubject(subject);
        message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(recipient));
        message.setHeader("Organization", organisation);
        DataSource source = new ByteArrayDataSource(getClass().getResourceAsStream(logoResourceName), "image/jpg");
        BodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent(htmlContent, "text/html");
        BodyPart dataPart = new MimeBodyPart();
        dataPart.setDataHandler(new DataHandler(source));
        dataPart.setHeader("Content-ID", "<image>");
        MimeMultipart multipart = new MimeMultipart("related");
        multipart.addBodyPart(htmlPart);
        multipart.addBodyPart(dataPart);
        message.setContent(multipart);
        if (false) {
            MimeBodyPart logoBodyPart = new MimeBodyPart();
            logoBodyPart.setHeader("Content-Type", "image/jpeg");
            logoBodyPart.setHeader("Content-ID", "<image>");
            logoBodyPart.setDataHandler(new DataHandler(source));
            logoBodyPart.setDisposition(MimeBodyPart.INLINE);
            logoBodyPart.setFileName(logoFileName);
            multipart.addBodyPart(logoBodyPart);
        }
        Transport.send(message);
    }   
}