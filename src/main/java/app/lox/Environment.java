package app.lox;

import app.impl.Key;

import java.util.LinkedHashMap;
import java.util.Map;

public class Environment {
  private final Map<Key, Object> values = new LinkedHashMap<>();

  public void define(Key name, Object value) {
    values.put(name, value);
  }

  public Object getOrDefault(Key key, Object defaultValue) {
    return values.getOrDefault(key, defaultValue);
  }
}
