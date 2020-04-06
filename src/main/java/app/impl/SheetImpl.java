package app.impl;

import app.Sheet;

import java.util.LinkedHashMap;

public class SheetImpl implements Sheet {
  private static final Cell DEF_CELL = Cell.of("");
  private final LinkedHashMap<String, Cell> cells = new LinkedHashMap<>();

  @Override
  public String get(String key) {
    return getCell(key).value;
  }

  @Override
  public void put(String key, String literal) {
    cells.put(key, Cell.of(literal));
  }

  @Override
  public String getLiteral(String key) {
    return getCell(key).literal;
  }

  private Cell getCell(String key) {
    return cells.getOrDefault(key, DEF_CELL);
  }
}

class Cell {
  final String literal, value;

  private Cell(String literal, String value) {
    this.literal = literal;
    this.value = value;
  }

  static Cell of(String literal) {
    String value;
    try {
      value = Integer.parseInt(literal.trim()) + "";
    } catch (NumberFormatException e) {
      value = literal;
    }
    return new Cell(literal, value);
  }
}
