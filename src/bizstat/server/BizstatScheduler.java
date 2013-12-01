/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package bizstat.server;

import bizstat.entity.BizstatService;
import bizstat.entity.Host;
import bizstat.entity.HostServiceKey;
import bizstat.entity.Network;
import vellum.datatype.Millis;
import vellum.util.Calendars;
import vellum.logr.Logr;
import vellum.logr.LogrFactory;
import java.util.*;
import vellum.format.DefaultDateFormats;

/**
 *
 * @author evan.summers
 */
public class BizstatScheduler implements Runnable {

    Logr logger = LogrFactory.getLogger(BizstatScheduler.class);
    
    BizstatServer server;

    public BizstatScheduler(BizstatServer server) {
        this.server = server;
    }

    @Override
    public void run() {
        for (Network network : server.networkList) {
            if (network.isEnabled()) {
                schedule(network);
            }
        }
    }  
    
    private void schedule(Network network) {
        for (Host host : network.getHostList()) {
            if (host.isEnabled()) {
                schedule(host);
            }
        }
    }
    
    private void schedule(Host host) {
        for (BizstatService service : host.getServiceList()) {
            if (service.isEnabled() && service.getScheduleTime() != null) {
                schedule(host, service);
            }
        }
    }
    
    private void schedule(Host host, BizstatService service) {
        Calendar calendar = Calendar.getInstance();
        Calendars.setTime(calendar, service.getScheduleTime());
        long currentMillis = System.currentTimeMillis();
        long scheduleMillis = calendar.getTimeInMillis();
        if (scheduleMillis < currentMillis) scheduleMillis += Millis.fromDays(1);
        long initialDelay =  scheduleMillis - currentMillis;
        if (initialDelay < 0) initialDelay += Millis.fromDays(1);
        long period = service.getIntervalMillis();
        logger.info("schedule", host, service, 
                DefaultDateFormats.timeSecondsFormat.format(service.getScheduleTime()), 
                calendar.getTime(), initialDelay, period);
        server.getStatus(new HostServiceKey(host, service)).schedule(initialDelay, period);
    }
    
      
    
}
