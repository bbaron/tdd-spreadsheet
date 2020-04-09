package app.impl;

import java.util.LinkedHashMap;

import static app.misc.Helpers.nullToEmpty;
import static app.misc.Helpers.stringify;

class Grid {
  private final LinkedHashMap<Key, Cell> cells = new LinkedHashMap<>();
  private final Environment environment = new Environment();
  private final Interpreter interpreter = new Interpreter(environment);

  String get(String key) {
    return stringify(environment.getOrDefault(Key.of(key), ""));
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
    literal = nullToEmpty(literal);
    String formula = literal;
    Object value = tryInteger(literal);
    if (value != null) {
      environment.define(key, value);
    } else if (!literal.startsWith("=")) {
      environment.define(key, literal);
      return new Cell(key, literal, Expr.variable(key));
    }

    if (literal.startsWith("=")) {
      formula = formula.substring(1);
    }

    Expr expr = interpreter.interpret(key, formula);
    return new Cell(key, literal, expr);
  }

  private static Integer tryInteger(String literal) {
    try {
      return Integer.parseInt(literal.trim());
    } catch (NumberFormatException e) {
      return null;
    }
  }

}

