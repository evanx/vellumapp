/*
 * Source https://github.com/evanx by @evanxsummers
 */
package crocserver.httphandler.secure;

import crocserver.storage.servicerecord.ServiceRecord;
import crocserver.app.CrocApp;
import crocserver.httphandler.common.AbstractPageHandler;
import crocserver.storage.adminuser.AdminUser;
import crocserver.storage.clientcert.Cert;
import crocserver.storage.common.CrocStorage;
import java.util.Collection;
import java.util.Iterator;
import vellum.data.Millis;
import vellum.html.HtmlPrinter;
import vellum.logr.LogrFactory;
import vellum.logr.LogrRecord;
import vellum.format.ListFormats;
import crocserver.storage.org.Org;
import crocserver.storage.service.Service;
import java.io.IOException;
import sun.security.x509.X500Name;
import vellum.format.CalendarFormats;
import vellum.logr.LogrLevel;

/**
 *
 * @author evan.summers
 */
public class SecureHomeHandler extends AbstractPageHandler {

    CrocApp app;
    CrocStorage storage;
    
    public SecureHomeHandler(CrocApp app) {
        super();
        this.app = app;
        this.storage = app.getStorage();
    }

    @Override
    protected void handle() throws Exception {
        h.div("menuBarDiv");
        h.aClosed("/", "Home");
        h.divClose();
        printOrgs("orgs", storage.getOrgStorage().getList());
        printUsers("admin users", storage.getUserStorage().getList());
        printServices("services", storage.getServiceStorage().getList());
        printCerts("certs", storage.getCertStorage().getList());
        printSeviceRecords("service records", storage.getServiceRecordStorage().getList());
        if (LogrFactory.getDefaultLevel().ordinal() < LogrLevel.INFO.ordinal()) {
            printLog("log", LogrFactory.getDequerProvider().getDequerHandler().getDequer().tailDescending(100));
        }
    }

    private void printOrgs(String label, Collection<Org> orgs) {
        h.h(3, label);
        h.tableDiv("resultSet");
        h.trh("id", "org name", "display name", "url", "updated");
        for (Org org : orgs) {
            h.trd(
                    String.format("<a href='/viewOrg/%d'>%d</a>", org.getId(), org.getId()),
                    org.getOrgName(),
                    org.getDisplayName(),
                    org.getUrl(),
                    CalendarFormats.timestampFormat.format(org.getUpdated()));
        }
        h.tableClose();
        h.divClose();
    }

    private void printUsers(String label, Collection<AdminUser> users) {
        h.h(3, label);
        h.tableDiv("resultSet");
        h.trh("id", "username", "display name", "email", "login");
        for (AdminUser user : users) {
            String orgHtml = "";
            h.trd(
                    String.format("<a href='/viewUser/%s'>%s</a>", user.getId(), user.getId()),
                    user.getUserName(),
                    user.getDisplayName(),
                    user.getEmail(),
                    CalendarFormats.timestampFormat.format(user.getLoginTime())
                    );
        }
        h.tableClose();
        h.divClose();
    }

    private void printServices(String label, Collection<Service> certs) {
        h.h(3, label);
        h.tableDiv("resultSet");
        h.trh("id", "org", "host", "client", "updated");
        for (Service cert : certs) {
            h.trd(
                    String.format("<a href='/viewService/%s'>%s</a>", cert.getId(), cert.getId()),
                    cert.getOrgId(),
                    cert.getHostName(),
                    cert.getServiceName(),
                    CalendarFormats.timestampFormat.format(cert.getUpdated())
                    );
        }
        h.tableClose();
        h.divClose();
    }

    private void printCerts(String label, Collection<Cert> certs) throws IOException {
        h.h(3, label);
        h.tableDiv("resultSet");
        h.trh("id", "subject", "updated");
        for (Cert cert : certs) {
            h.trd(
                    String.format("<a href='/viewCert/%s'>%s</a>", cert.getId(), cert.getId()),
                    new X500Name(cert.getSubject()).getCommonName(),
                    CalendarFormats.timestampFormat.format(cert.getUpdated())
                    );
        }
        h.tableClose();
        h.divClose();
    }
    
    private void printSeviceRecords(String label, Collection<ServiceRecord> serviceRecords) {
        h.h(3, label);
        h.tableDiv("resultSet");
        for (ServiceRecord serviceRecord : serviceRecords) {
            h.trd(
                    String.format("<a href='/viewServiceRecord/%d'>%d</a>", serviceRecord.getId(), serviceRecord.getId()),
                    Millis.formatPeriod(serviceRecord.getTimestamp()),
                    serviceRecord.getCertName(),
                    serviceRecord.getServiceName(),
                    serviceRecord.getServiceStatus());
        }
        h.tableDivClose();
    }

    private void printLog(String label, Collection<LogrRecord> records) {
        printLog(label, records.iterator());
    }

    private void printLog(String label, Iterator<LogrRecord> iterator) {
        HtmlPrinter p = new HtmlPrinter(out);
        p.h(3, label);
        p.tableDiv("resultSet");
        p.thead();
        p.theadClose();
        p.tbody();
        while (iterator.hasNext()) {
            LogrRecord record = iterator.next();
            p.trd(Millis.formatAsSeconds(record.getTimestamp()),
                    record.getContext().getName(),
                    record.getLevel(), record.getMessage(),
                    ListFormats.displayFormatter.formatArray(record.getArgs()));
        }
        p.tbodyClose();
        p.tableDivClose();
    }
}
