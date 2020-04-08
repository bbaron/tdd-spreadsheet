package app.lox;

import java.util.LinkedHashMap;
import java.util.Map;

class Environment {
  private final Map<String, Object> values = new LinkedHashMap<>();

  void define(String name, Object value) {
    values.put(name, value);
  }

  Object get(Token name) {
    if (values.containsKey(name.lexeme)) {
      return values.get(name.lexeme);
    }

    throw new RuntimeError(name, "Undefined variable '" + name.lexeme + "'.");
  }
}
