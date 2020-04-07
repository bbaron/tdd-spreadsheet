package app.impl;

import app.Sheet;

import java.util.LinkedHashMap;

public class SheetImpl implements Sheet {
  private final LinkedHashMap<String, Cell> cells = new LinkedHashMap<>();

  @Override
  public String get(String key) {
    return getCell(key).value;
  }

  @Override
  public void put(String key, String literal) {
    cells.put(normalize(key), Parser.parse(literal));
  }

  @Override
  public String getLiteral(String key) {
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

