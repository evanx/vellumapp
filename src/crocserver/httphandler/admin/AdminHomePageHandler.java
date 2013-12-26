/*
 * Source https://github.com/evanx by @evanxsummers
 */
package crocserver.httphandler.admin;

import crocserver.app.CrocMessageBuilder;
import crocserver.storage.servicerecord.ServiceRecord;
import crocserver.httphandler.common.AbstractPageHandler;
import java.util.Collection;
import java.util.Iterator;
import vellum.format.DefaultDateFormats;
import vellum.util.Strings;
import java.util.List;
import vellum.data.Millis;
import vellum.html.HtmlPrinter;
import vellumexp.logr.LogrFactory;
import vellumexp.logr.LogrRecord;
import vellum.format.ListFormats;
import crocserver.storage.common.CrocStorage;
import vellumexp.logr.LogrLevel;

/**
 *
 * @author evan.summers
 */
public class AdminHomePageHandler extends AbstractPageHandler {

    CrocStorage storage;

    public AdminHomePageHandler(CrocStorage storage) {
        super();
        this.storage = storage;
    }

    @Override
    protected void handle() throws Exception {
        selectStatus();
        if (LogrFactory.getDefaultLevel().ordinal() < LogrLevel.INFO.ordinal()) {
            printLog(LogrFactory.getDequerProvider().getDequerHandler().getDequer().tailDescending(100));
        }
    }

    private void selectStatus() throws Exception {
        print("stored status", storage.getServiceRecordStorage().getList());
    }

    private void print(String label, Collection<ServiceRecord> serviceRecords) {
        out.printf("<h3>%s</h3>\n", label);
        out.printf("<div class='resultSet'>\n");
        out.printf("<table>\n");
        int index = 0;
        for (ServiceRecord serviceRecord : serviceRecords) {
            out.printf("<tr class=row%d><td><a href='/view/serviceRecord/%d'>%d</a><td>%s<td>%s<td><b>%s</b><td>%s<td>%s\n",
                    ++index % 2,
                    serviceRecord.getId(),
                    serviceRecord.getId(),
                    Millis.formatAsSeconds(serviceRecord.getTimestamp()),
                    serviceRecord.getCertName(),
                    serviceRecord.getServiceName(),
                    serviceRecord.getServiceStatus(),
                    CrocMessageBuilder.buildOutText(serviceRecord));
        }
        out.printf("</table>\n");
        out.printf("</div>\n");
    }

    private void print(List<LogrRecord> records) {
        out.printf("<h3>log</h3>\n");
        out.printf("<table class='resultSet'>\n");
        int rowCount = 0;
        for (LogrRecord message : records) {
            out.printf("<tr class=row%d>\n", ++rowCount % 2);
            String string = Strings.escapeXml(message.getMessage().trim());
            string = string.trim();
            StringBuilder detailBuilder = new StringBuilder();
            if (string.length() > 0) {
                detailBuilder.append(string);
            }
            if (message.getMessage() != null) {
                if (detailBuilder.length() > 0) {
                    detailBuilder.append("<br>");
                }
                detailBuilder.append("<pre>");
                detailBuilder.append(Strings.escapeXml(message.getMessage().trim()));
                detailBuilder.append("</pre>");
            }
            out.printf("<td>%s<td>%s<td><b>%s</b><td>%s\n",
                    DefaultDateFormats.formatDateTimeSeconds(message.getTimestamp()),
                    message.getLevel(),
                    message.getContext().getName(),
                    detailBuilder.toString());
        }
        out.printf("</table>\n");
    }

    private void printLog(Collection<LogrRecord> records) {
        printLog(records.iterator());
    }
    
    private void printLog(Iterator<LogrRecord> iterator) {
       HtmlPrinter p = new HtmlPrinter(out);
       p.h(3, "log");
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
