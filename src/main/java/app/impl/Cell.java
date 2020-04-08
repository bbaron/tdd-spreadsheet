package app.impl;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

class Cell {
  private static final ConcurrentMap<Key, Cell> emptyCells = new ConcurrentHashMap<>();
  static final String DEFAULT_LITERAL = "";
  static final String DEFAULT_VALUE = "";

  final Key key;
  final String literal, value;

  Cell(Key key, String literal, String value) {
    this.key = key;
    this.literal = literal;
    this.value = value;
  }

  private Cell(Key key) {
    this(key, DEFAULT_LITERAL, DEFAULT_VALUE);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(key);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!(obj instanceof Cell)) return false;
    Cell that = (Cell) obj;
    return Objects.equals(this.key, that.key);
  }

  @Override
  public String toString() {
    return String.format("Cell(key = '%s', literal = '%s', value = '%s')", key, literal, value);
  }

  static Cell empty(Key key) {
    return emptyCells.computeIfAbsent(key, Cell::new);
  }
}
