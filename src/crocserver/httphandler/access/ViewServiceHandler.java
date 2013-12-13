/*
 * Source https://github.com/evanx by @evanxsummers
 */
package crocserver.httphandler.access;

import crocserver.httphandler.common.AbstractPageHandler;
import vellum.html.HtmlPrinter;
import crocserver.storage.common.CrocStorage;
import crocserver.storage.service.Service;

/**
 *
 * @author evan.summers
 */
public class ViewServiceHandler extends AbstractPageHandler {

    CrocStorage storage;

    public ViewServiceHandler(CrocStorage storage) {
        super();
        this.storage = storage;
    }

    @Override
    protected void handle() throws Exception {
        Long id = Long.parseLong(pathArgs[1]);
        HtmlPrinter p = new HtmlPrinter(out);
        p.div("menuBarDiv");
        p.aClosed("/", "Home");
        p.divClose();
        p.span("pageTitle", String.format("Service %s", id));
        Service service = storage.getServiceStorage().find(id);
        p.tableDiv("resultSet");
        p.thead();
        p.theadClose();
        p.tbody();
        p.trhd("Org", service.getOrgId());
        p.trhd("Host", service.getHostName());
        p.trhd("Client", service.getServiceName());
        p.trhd("Updated", service.getUpdated());
        p.trhd("Updated by", service.getUpdatedBy());
        p.tbodyClose();
        p.tableDivClose();
        p.pre(service.getCert());
    }
}
