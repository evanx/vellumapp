/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package mobi.mail;

import vellum.util.Streams;
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
import mobi.entity.Person;
import vellum.logr.Logr;
import vellum.logr.LogrFactory;

/**
 *
 * @author evan.summers
 */
public class Mailer {
    static Logr logger = LogrFactory.getLogger(Mailer.class);

    String logoFileName = "image.jpg";
    String logoResourceName = "giftbox.jpg";
    String organisation = "giftme.mobi";
    String from = "mailer@GiftMe.mobi";
    String username = "giftmej";
    String password = "30eewb8s";
    String host = "localhost";
    int port = 25;
    String localhost = "giftme.mobi";
    
    public void sendEmail(String recipient, String subject, String htmlContent) throws Exception {
        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.localhost", localhost);
        props.put("mail.smtp.auth", "true");
        logger.info("sendEmail", props);
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
        logger.info("sendEmail", recipient, subject);
    }
    
    public void sendWelcomeEmail(Person person) throws Exception {
        String html = Streams.readString(getClass().getResourceAsStream("welcome.html"));
        html = html.replace("${name}", person.getPersonName());
        sendEmail(person.getEmail(), "Welcome to GiftMe.mobi", html);
    }

    public void sendWelcomeEmail(String email, String personName) throws Exception {
        String html = Streams.readString(getClass().getResourceAsStream("welcome.html"));
        html = html.replace("${name}", personName);
        sendEmail(email, "Welcome to GiftMe.mobi", html);
    }
    
    public void startWelcomeEmail(final String email, final String personName) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    sendWelcomeEmail(email, personName);
                } catch (Exception e) {
                    logger.warn(e);
                }
            }
        }).start();
    }
    
}