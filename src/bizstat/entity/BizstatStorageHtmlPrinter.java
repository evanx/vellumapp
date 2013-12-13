/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package bizstat.entity;

import bizstat.server.BizstatServer;
import vellum.entity.EntityMap;
import vellum.entity.HasParent;
import vellum.entity.IdEntity;
import java.io.PrintStream;

/**
 *
 * @author evan.summers
 */
public class BizstatStorageHtmlPrinter {

    BizstatServer context;
    BizstatConfigStorage storage;
    PrintStream stream;

    public BizstatStorageHtmlPrinter(BizstatServer context) {
        this.context = context;
        this.storage = context.getConfigStorage();
    }

    public void print(PrintStream stream) {
        this.stream = stream;
        for (Class entityType : storage.entityTypeMap.keySet()) {    
            EntityMap entityMap = storage.entityTypeMap.get(entityType);
            print(entityType, entityMap);
        }
    }

    private <I extends Comparable, E extends IdEntity> void print(Class entityType, EntityMap<I, E> entityMap) {
        stream.printf("\n<h2>%s</h2>\n", entityType.getSimpleName());
        for (E entity : entityMap.getExtentList()) {
            stream.println(format(entity));
        }
    }
    
    private String format(IdEntity entity) {
        if (entity instanceof HasParent) {
        }
        return entity.toString();
    }
}
