package app.impl;

class Cell {
  public static final Cell DEFAULT = new Cell("", "");
  final String literal, value;

  Cell(String literal, String value) {
    this.literal = literal;
    this.value = value;
  }

}
