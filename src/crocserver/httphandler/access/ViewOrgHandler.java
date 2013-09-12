/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 */
package crocserver.httphandler.access;

import crocserver.httphandler.common.AbstractPageHandler;
import vellum.html.HtmlPrinter;
import crocserver.storage.common.CrocStorage;
import crocserver.storage.org.Org;

/**
 *
 * @author evan.summers
 */
public class ViewOrgHandler extends AbstractPageHandler {
    CrocStorage storage;

    public ViewOrgHandler(CrocStorage storage) {
        super();
        this.storage = storage;
    }

    @Override
    protected void handle() throws Exception {
       HtmlPrinter p = new HtmlPrinter(out);
       long id = Long.parseLong(pathArgs[1]);
       p.div("menuBarDiv");
       p.aClosed("/", "Home");
       p.divClose();
       p.span("pageTitle", String.format("Org %s", id));
       Org org = storage.getOrgStorage().get(id);
       p.tableDiv("resultSet");
       p.thead();
       p.theadClose();
       p.tbody();
       p.trhd("Org name", org.getId());
       p.trhd("Display name", org.getDisplayName());
       p.trhd("Url", org.getUrl());
       p.trhd("Updated", org.getUpdated());
       p.tbodyClose();
       p.tableDivClose();
    }    
}
