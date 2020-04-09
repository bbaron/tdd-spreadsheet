package app.impl;

import app.misc.SheetLogger;
import app.misc.StdOutLogger;

import java.util.LinkedHashMap;

import static app.misc.Helpers.nullToEmpty;
import static app.misc.Helpers.stringify;

class Grid {
  private final LinkedHashMap<Key, Cell> cells = new LinkedHashMap<>();
  private final Environment environment = new Environment();
  private final References references = new References();
  private final Interpreter interpreter = new Interpreter(environment, references);
  private final SheetLogger logger = new StdOutLogger(SheetLogger.Verbosity.DEBUG, getClass());

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
    var dependsOn = references.getDependsOn(key);
    var referencedBy = references.getReferencedBy(key);
    if (!dependsOn.isEmpty()) {
      logger.debug("key: %s dependsOn: %s", key, dependsOn);
    }
    if (!referencedBy.isEmpty()) {
      logger.debug("key %s is referenced by %s", key, referencedBy);
      for (Key k : referencedBy) {
        Cell c = cells.get(k);
        Object result = interpreter.evaluate(c.expr);
        logger.debug("%s is re-evaluated to %s", k, result);
        environment.define(k, result);
      }
    }
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

