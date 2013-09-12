/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package bizstat.entity;

import bizstat.server.BizstatServer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import vellum.entity.EntityMap;
import vellum.entity.IdEntity;
import java.io.PrintStream;

/**
 *
 * @author evan.summers
 */
public class BizstatStorageJsonPrinter {

    BizstatServer context;
    BizstatConfigStorage storage;
    PrintStream stream;
    GsonBuilder builder = new GsonBuilder();
    Gson gson; 
    
    public BizstatStorageJsonPrinter(BizstatServer context) {
        this.context = context;
        this.storage = context.getConfigStorage();
        builder.setPrettyPrinting();
        gson = builder.create();
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
            stream.println(gson.toJson(entity));
        }
    }
    
}
