package jelly.storage;

import jx.JxMap;

/**
 *
 * @author evan.summers
 */
public interface Storage {
    public void put(String database, String type, Comparable id, JxMap data);
    public JxMap get(String database, String type, Comparable id);
    public JxMap find(String database, String type, Comparable id) throws StorageException;
    
}
