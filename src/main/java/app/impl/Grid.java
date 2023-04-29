package app.impl;

import app.CellChangeNotifier;
import app.misc.SheetLogger;
import app.misc.StdOutLogger;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Queue;
import java.util.Set;

import static app.misc.Helpers.nullToEmpty;
import static app.misc.Helpers.stringify;

class Grid {
  private final LinkedHashMap<Key, Cell> cells = new LinkedHashMap<>();
  private final Environment environment = new Environment();
  private final References references = new References();
  private final Interpreter interpreter = new Interpreter(environment, references);
  private final SheetLogger logger = new StdOutLogger(SheetLogger.Verbosity.DEBUG, getClass());
  private final CellChangeNotifier notifier;

  Grid(CellChangeNotifier notifier) {
    this.notifier = notifier;
  }

  String get(String key) {
    return stringify(environment.getOrDefault(Key.of(key), ""));
  }

  void put(String key, String literal) {
    Key k = Key.of(key);
    references.clearReferences(k);
    cells.put(k, parse(k, literal));
  }

  String getLiteral(String key) {
    return getCell(key).literal;
  }

  private Cell getCell(String key) {
    return getCell(Key.of(key));
  }

  private Cell getCell(Key key) {
    return cells.getOrDefault(key, Cell.empty(key));
  }

  private Cell parse(Key key, String literal) {
    literal = nullToEmpty(literal);
    String formula = literal;
    Object value = tryInteger(literal);
    if (value != null) {
      define(key, value);
    } else if (!literal.startsWith("=")) {
      define(key, literal);
      return new Cell(key, literal, Expr.variable(key));
    }

    if (literal.startsWith("=")) {
      formula = formula.substring(1);
    }

    Expr expr = interpreter.interpret(key, formula);
    recalculate(key);
    return new Cell(key, literal, expr);
  }

  private void recalculate(final Key key) {
    Set<Key> marked = new HashSet<>();
    Queue<Key> queue = new ArrayDeque<>(references.getReferencedBy(key));
    while (!queue.isEmpty()) {
      Key v = queue.remove();
      marked.add(v);
      Cell c = getCell(v);
      if (c != null) {
        if (c.expr != null) {
          Object result = interpreter.dispatch(c.expr);
          logger.debug("%s is re-evaluated to %s", v, result);
          define(v, result);
        } else {
          logger.debug("%s does not have an expr", key);
        }
      } else {
        logger.debug("%s does not have a cell", key);
      }
      for (Key w : references.getReferencedBy(v)) {
        if (!marked.contains(w)) {
          queue.add(w);
        }
      }
    }
  }

  private void define(Key key, Object value) {
    environment.define(key, value);
    int row = key.getRowIndex();
    int col = key.getColumnIndex();
    System.out.printf("cellChangedAt %s %s %s%n", key, row, col);
    notifier.cellChangedAt(key.getRowIndex(), key.getColumnIndex());
  }

  private static Integer tryInteger(String literal) {
    try {
      return Integer.parseInt(literal.trim());
    } catch (NumberFormatException e) {
      return null;
    }
  }

}

