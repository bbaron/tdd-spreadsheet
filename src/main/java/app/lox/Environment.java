package app.lox;

import app.impl.Key;

import java.util.LinkedHashMap;
import java.util.Map;

class Environment {
  private final Map<Key, Object> values = new LinkedHashMap<>();

  void define(Key name, Object value) {
    values.put(name, value);
  }

  Object get(Token name) {
    return values.computeIfAbsent(name.key, k -> 0.0);
  }
}
