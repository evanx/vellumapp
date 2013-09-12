/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package bizstat.storage.servicerecord;

import bizstat.entity.ServiceRecord;
import bizstat.storage.BizstatStorage;
import crocserver.storage.org.Org;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author evan.summers
 */
public class ServiceRecordStorage {
    BizstatStorage storage;
    
    public ServiceRecordStorage(BizstatStorage storage) {
        this.storage = storage;
    }

    public void insert(Org org, ServiceRecord serviceRecord) throws SQLException {
    }

    public Collection<ServiceRecord> getList() {
        List list = new ArrayList();
        return list;
    }
    
}
