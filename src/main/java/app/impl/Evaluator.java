package app.impl;

import app.lox.Interpreter;

class Evaluator {
  private final Interpreter interpreter = new Interpreter();

  private static Integer tryInteger(String literal) {
    try {
      return Integer.parseInt(literal.trim());
    } catch (NumberFormatException e) {
      return null;
    }
  }



  Cell parse(String literal) {
    if (literal == null || literal.isEmpty()) return Cell.DEFAULT;
    Object value = tryInteger(literal);
    if (value != null) return new Cell(literal, value.toString());

    if (literal.charAt(0) == '=' && literal.length() > 1) {
      String formula = literal.substring(1);
      String result = interpreter.interpret(formula);
      //      value = tryInteger(literal.substring(1));
//      if (value == null) throw new IllegalStateException();
      return new Cell(literal, result);
    }
    return new Cell(literal, literal);
  }
}
