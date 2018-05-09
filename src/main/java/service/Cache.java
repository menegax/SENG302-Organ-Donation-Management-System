package service;

import java.util.HashMap;
import java.util.Map;

public class Cache {

    private static final Map<String, Object> cache = new HashMap<>();

    /**
     * Adds entries to the map
     * @param key - key to identify the value by
     * @param value - value to store
     */
    public static void add(String key, Object value) {
        if (key != null && value != null){
            cache.put(key, value);
        }
    }

    /**
     * Remove an entry from the map
     * @param key - key value to be removed
     */
    public static void remove(String key) {
        if (cache.get(key) != null) {
            cache.remove(key);
        }
    }

    /**
     * Returns the object at the given key
     * @param key - key to identify the object value by
     * @return - object at the given key
     */
    public static Object get(String key) {
        return cache.get(key);
    }

    /**
     * Clears the map of all entries
     */
    public static void clear() {
        cache.clear();
    }

}
