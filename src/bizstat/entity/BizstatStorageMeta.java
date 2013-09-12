/*
 */
package bizstat.entity;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author evan.summers
 */
public class BizstatStorageMeta {
    Class[] types = {
        Contact.class, 
        ContactGroup.class, 
        BizstatService.class, 
        ServicePath.class, 
        Host.class, 
        Network.class,
    };
    Map<String, Class> typeMap = new HashMap();

    public BizstatStorageMeta() {
        for (Class type : types) {
            typeMap.put(type.getSimpleName(), type);
        }
    }

    public Map<String, Class> getTypeMap() {
        return typeMap;
    }
    
}
