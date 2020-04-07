package app.impl;

import java.util.Optional;

class Parser {
  private static Integer tryInteger(String literal) {
    try {
      return Integer.parseInt(literal.trim());
    } catch (NumberFormatException e) {
      return null;
    }
  }

  static Cell parse(String literal) {
    if (literal == null || literal.isEmpty()) return Cell.DEFAULT;
    Object value = tryInteger(literal);
    if (value != null) return new Cell(literal, value.toString());
    if (literal.charAt(0) == '=' && literal.length() > 1) {
      value = tryInteger(literal.substring(1));
      if (value == null) throw new IllegalStateException();
      return new Cell(literal, value.toString());
    }
    return new Cell(literal, literal);
  }
}
