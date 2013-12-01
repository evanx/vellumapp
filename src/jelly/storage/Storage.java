package jelly.storage;

import vellum.jx.JMap;

/**
 *
 * @author evan.summers
 */
public interface Storage {
    public void put(String database, String type, Comparable id, JMap data);
    public JMap get(String database, String type, Comparable id);
    public JMap find(String database, String type, Comparable id) throws StorageException;
    
}
