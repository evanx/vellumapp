/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package bizstat.server;

import bizstat.entity.ServiceRecord;
import java.util.Comparator;
import java.util.TreeSet;

/**
 *
 * @author evan.summers
 */
public class ServiceRecordSet extends TreeSet<ServiceRecord> {

    public ServiceRecordSet() {
        super(new Comparator<ServiceRecord>() {

            @Override
            public int compare(ServiceRecord o1, ServiceRecord o2) {
                return o1.getHostServiceKey().compareTo(o2.getHostServiceKey());
            }
        });
    }
}
