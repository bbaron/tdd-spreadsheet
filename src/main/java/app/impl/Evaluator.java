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


  Cell parse(String key, String literal) {
    if (literal == null || literal.isEmpty()) return Cell.DEFAULT;
    String formula = literal;
    Object value = tryInteger(literal);
    if (value != null) {
      formula = "=" + value;
    } else if (literal.charAt(0) != '='){
      return new Cell(literal, literal);
    }

    if (formula.charAt(0) == '=' && formula.length() > 1) {
      formula = formula.substring(1);
    }

    String result = interpreter.interpret(key, formula);
    return new Cell(literal, result);
  }
}
