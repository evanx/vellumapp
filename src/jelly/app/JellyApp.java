package jelly.app;

import jelly.storage.Storage;
import jelly.storage.TemporaryStorage;


/**
 *
 * @author evan.summers
 */
public class JellyApp {
    Storage storage;
    
    public void init() {
        storage = new TemporaryStorage();
    }

    public Storage getStorage() {
        return storage;
    }        
}
