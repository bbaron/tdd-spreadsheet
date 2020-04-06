package app;

import java.util.LinkedHashMap;

class Sheet {
  private final LinkedHashMap<String, String> cells = new LinkedHashMap<>();

  String get(String key) {
    String literal = getLiteral(key);
    try {
      return Integer.parseInt(literal.trim()) + "";
    } catch (NumberFormatException e) {
      return literal;
    }
  }

  public void put(String key, String value) {
    cells.put(key, value);
  }

  public String getLiteral(String key) {
    return cells.getOrDefault(key, "");
  }
}
