package com.iip.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Demo on 4/13/2017.
 */
public class ViewObject {
    private Map<String, Object> map = new HashMap<>();

    public void set(String key, Object value) {
        map.put(key, value);
    }

    public Object get(String key) {
        return map.get(key);
    }
}
