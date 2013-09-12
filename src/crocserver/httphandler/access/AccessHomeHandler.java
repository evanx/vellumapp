/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 */
package crocserver.httphandler.access;

import crocserver.app.CrocApp;
import crocserver.httphandler.common.AbstractPageHandler;
import crocserver.storage.service.Service;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import vellum.format.CalendarFormats;

/**
 *
 * @author evan.summers
 */
public class AccessHomeHandler extends AbstractPageHandler {

    CrocApp app;

    public AccessHomeHandler(CrocApp app) {
        super();
        this.app = app;
    }

    @Override
    public void handle() throws IOException, SQLException {
        h.div("header");
        h.span("brand", "croc");
        h.divClose();
        h.divId("watermark");
        h.divClose();
        h.img("http://upload.wikimedia.org/wikipedia/commons/5/55/Gnome-security-medium.svg");
        h.div("menuBarDiv");
        h.aClosed("/", "Home");
        h.span("style", "|");
        h.aClosed(app.getGoogleApi().getLoginUrl(), "Login with Google");
        h.divClose();
        if (false) {
            String qrUrl = "https://www.google.com/chart?chs=200x200&chld=M|0&cht=qr&chl=otpauth:%3A%2F%2Ftotp%2Fevans%3Fsecret%3DAAAAAAAAAAAAAAAAA";
            logger.info("qrUrl", qrUrl);
            h.img(qrUrl);
            h.pre(qrUrl);
        }        
        printCerts("certs", app.getStorage().getServiceStorage().getList());
    }
    
    private void printCerts(String label, Collection<Service> certs) {
        h.h(3, label);
        h.tableDiv("resultSet");
        h.trh("id", "org", "host", "client", "updated", "updated by");
        for (Service cert : certs) {
            h.trd(
                    String.format("<a href='/view/cert/%s'>%s</a>", cert.getId(), cert.getId()),
                    cert.getOrgId(),
                    cert.getHostName(),
                    cert.getServiceName(),
                    CalendarFormats.timestampFormat.format(cert.getUpdated()),
                    cert.getUpdatedBy());
        }
        h.tableClose();
        h.divClose();
    }      
}
