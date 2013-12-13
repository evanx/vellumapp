/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package bizstat.server;

import bizstat.entity.Network;
import vellum.system.Systems;
import vellum.logr.Logr;
import vellum.logr.LogrFactory;
import java.util.*;

/**
 *
 * @author evan.summers
 */
public class BizstatDispatcher implements Runnable {

    Logr logger = LogrFactory.getLogger(BizstatDispatcher.class);
    
    BizstatServer server;

    Map<Network, BizstatNetworkDispatcher> networkDispatcherMap = new HashMap();
        
    public BizstatDispatcher(BizstatServer server) {
        this.server = server;
    }

    @Override
    public void run() {
        for (Network network : server.networkList) {
            if (network.isEnabled()) {
                BizstatNetworkDispatcher networkDispatcher = 
                        new BizstatNetworkDispatcher(server, network);
                networkDispatcherMap.put(network, networkDispatcher);
                networkDispatcher.run();
                Systems.sleep(server.config.sleepMillis/server.networkList.size());
            }
        }
    }  
}
