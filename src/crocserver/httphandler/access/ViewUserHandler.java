/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 */
package crocserver.httphandler.access;

import crocserver.httphandler.common.AbstractPageHandler;
import crocserver.storage.adminuser.AdminUser;
import vellum.html.HtmlPrinter;
import crocserver.storage.common.CrocStorage;

/**
 *
 * @author evan.summers
 */
public class ViewUserHandler extends AbstractPageHandler {
    CrocStorage storage;

    public ViewUserHandler(CrocStorage storage) {
        super();
        this.storage = storage;
    }

    @Override
    protected void handle() throws Exception {
       HtmlPrinter p = new HtmlPrinter(out);
       String id = pathArgs[1];
       p.div("menuBarDiv");
       p.aClosed("/", "Home");
       p.divClose();
       p.span("pageTitle", String.format("User %s", id));
       AdminUser user = storage.getUserStorage().find(id);
       p.tableDiv("resultSet");
       p.thead();
       p.theadClose();
       p.tbody();
       p.trhd("Username", user.getId());
       p.trhd("Display name", user.getDisplayName());
       p.trhd("Email", user.getEmail());
       p.trhd("Cert subject", user.getSubject());
       p.tbodyClose();
       p.tableDivClose();
    }    
}
