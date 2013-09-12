/*
 */
package jx;

import com.google.gson.Gson;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author evan.summers
 */
public class JxMap extends HashMap<String, Object> {
    
    public JxMap() {        
    }

    public Collection getCollection(String key) {
        return (Collection) super.get(key);
    }

    public Map getMap(String key) {
        return (Map) super.get(key);
    }
    
    public int getInt(String key, int defaultValue) {
        return Convertors.coerceInt(super.get(key), defaultValue);
    }

    public Integer getInteger(String key) {
        return Convertors.coerceInteger(super.get(key), null);
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
