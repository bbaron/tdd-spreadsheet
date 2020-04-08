package app.impl;

import app.lox.Environment;
import app.lox.Interpreter;

import java.util.LinkedHashMap;

class Grid {
  private final LinkedHashMap<Key, Cell> cells = new LinkedHashMap<>();
  private final Environment environment = new Environment();
  private final Interpreter interpreter = new Interpreter(environment);

  String get(String key) {
    return getCell(key).value;
  }

  void put(String key, String literal) {
    Key k = Key.of(key);
    cells.put(k, parse(k, literal));
  }

  String getLiteral(String key) {
    return getCell(key).literal;
  }

  private Cell getCell(String key) {
    Key k = Key.of(key);
    return cells.getOrDefault(k, Cell.empty(k));
  }

  private Cell parse(Key key, String literal) {
    if (literal == null || literal.isEmpty()) return Cell.empty(key);
    String formula = literal;
    Object value = tryInteger(literal);
    if (value != null) {
      environment.define(key, value);
    } else if (literal.charAt(0) != '=') {
      return new Cell(key, literal, literal);
    }

    if (formula.charAt(0) == '=' && formula.length() > 1) {
      formula = formula.substring(1);
    }

    String result = interpreter.interpret(key, formula);
    return new Cell(key, literal, result);
  }

  private static Integer tryInteger(String literal) {
    try {
      return Integer.parseInt(literal.trim());
    } catch (NumberFormatException e) {
      return null;
    }
  }

}

