package app.impl;

import java.util.LinkedHashMap;

class Grid {
  private final LinkedHashMap<String, Cell> cells = new LinkedHashMap<>();
  private final Evaluator evaluator = new Evaluator();

  String get(String key) {
    return getCell(key).value;
  }

  void put(String key, String literal) {
    cells.put(normalize(key), evaluator.parse(key, literal));
  }

  String getLiteral(String key) {
    return getCell(key).literal;
  }

  private Cell getCell(String key) {
    return cells.getOrDefault(normalize(key), Cell.DEFAULT);
  }

  private static String normalize(String key) {
    if (key == null) return null;
    return key.toUpperCase();
  }
}

