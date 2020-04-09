package app.impl;

import java.util.LinkedHashMap;
import java.util.Map;

class Environment {
  private final Map<Key, Object> values = new LinkedHashMap<>();

  void define(Key name, Object value) {
    values.put(name, value);
  }

  Object getOrDefault(Key key, Object defaultValue) {
    return values.getOrDefault(key, defaultValue);
  }
}
