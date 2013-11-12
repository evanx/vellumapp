package jelly.storage;

import java.util.HashMap;
import java.util.Map;
import jx.JxMap;
import vellum.logging.ArrayLogger;
import vellum.type.ComparableTuple;

/**
 *
 * @author evan.summers
 */
public class TemporaryStorage implements Storage {
    ArrayLogger logger = ArrayLogger.getLogger(TemporaryStorage.class);
    
    Map<Comparable, JxMap> store = new HashMap();
    
    @Override
    public void put(String database, String type, Comparable id, JxMap data) {
        store.put(ComparableTuple.create(database, type, id), data);
    }
    
    @Override
    public JxMap get(String database, String type, Comparable id) {
        JxMap data = store.get(ComparableTuple.create(database, type, id));
        if (data == null) {
            logger.debug("get", database, type, id);
        }
        return data;
    }

    @Override
    public JxMap find(String database, String type, Comparable id) throws StorageException {
        JxMap data = store.get(ComparableTuple.create(database, type, id));
        if (data == null) {
            throw new StorageException(StorageExceptionType.NOT_FOUND, database, type, id);
        }
        return data;
    }
    
}
