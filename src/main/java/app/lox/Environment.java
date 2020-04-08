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
    if (values.containsKey(name.key)) {
      return values.get(name.key);
    }

    throw new RuntimeError(name, "Undefined key '" + name.key + "'.");
  }
}
