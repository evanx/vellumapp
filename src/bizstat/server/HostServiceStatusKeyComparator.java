/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 */
package bizstat.server;

import bizstat.entity.HostServiceStatus;
import java.util.Comparator;

/**
 *
 * @author evan.summers
 */
public class HostServiceStatusKeyComparator implements Comparator<HostServiceStatus> {

    @Override
    public int compare(HostServiceStatus o1, HostServiceStatus o2) {
        return o1.getHostServiceKey().compareTo(o2.getHostServiceKey());
    }
}
