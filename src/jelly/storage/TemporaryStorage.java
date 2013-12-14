package jelly.storage;

import java.util.HashMap;
import java.util.Map;
import vellum.jx.JMap;
import vellum.logging.ArrayLogger;
import vellum.data.ComparableTuple;

/**
 *
 * @author evan.summers
 */
public class TemporaryStorage implements Storage {
    ArrayLogger logger = ArrayLogger.getLogger(TemporaryStorage.class);
    
    Map<Comparable, JMap> store = new HashMap();
    
    @Override
    public void put(String database, String type, Comparable id, JMap data) {
        store.put(ComparableTuple.create(database, type, id), data);
    }
    
    @Override
    public JMap get(String database, String type, Comparable id) {
        JMap data = store.get(ComparableTuple.create(database, type, id));
        if (data == null) {
            logger.debug("get", database, type, id);
        }
        return data;
    }

    @Override
    public JMap find(String database, String type, Comparable id) throws StorageException {
        JMap data = store.get(ComparableTuple.create(database, type, id));
        if (data == null) {
            throw new StorageException(StorageExceptionType.NOT_FOUND, database, type, id);
        }
        return data;
    }
    
}
