package app.impl;

import java.util.LinkedHashMap;

class Grid {
  private final LinkedHashMap<Key, Cell> cells = new LinkedHashMap<>();
  private final Evaluator evaluator = new Evaluator();

  String get(String key) {
    return getCell(key).value;
  }

  void put(String key, String literal) {
    Key k = Key.of(key);
    cells.put(k, evaluator.parse(k, literal));
  }

  String getLiteral(String key) {
    return getCell(key).literal;
  }

  private Cell getCell(String key) {
    Key k = Key.of(key);
    return cells.getOrDefault(k, Cell.empty(k));
  }

}

