/*
 */
package bizstat.entity;

import vellum.type.ComparableTuple;

/**
 *
 * @author evan.summers
 */
public class HostServiceKey extends ComparableTuple {
    Host host;
    BizstatService service;

    public HostServiceKey(Host host, BizstatService service) {
        super(new Comparable[] {host.getId(), service.getId()});
        this.host = host;
        this.service = service;
    }

    public Host getHost() {
        return host;
    }

    public BizstatService getService() {
        return service;
    }
}
