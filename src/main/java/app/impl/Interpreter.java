package app.impl;

import app.exceptions.RuntimeError;
import app.exceptions.SheetError;
import app.misc.SheetLogger;
import app.misc.StdOutLogger;

import java.util.List;

import static app.misc.SheetLogger.Verbosity.DEBUG;

class Interpreter implements Expr.Visitor<Object> {
  private final Environment environment;
  private final SheetLogger logger = new StdOutLogger(DEBUG, getClass());

  Interpreter(Environment environment) {
    this.environment = environment;
  }

  Expr interpret(Key key, String formula) {
    try {
      Scanner scanner = new Scanner(formula);
      List<Token> tokens = scanner.scanTokens();
      Parser parser = new Parser(tokens, key);
      Expr expression = parser.parse();
      Object value = evaluate(expression);
      environment.define(key, value);
      return expression;
    } catch (SheetError e) {
      logger.debug(e, "error in interpret");
      environment.define(key, "#Error");
      return null;
    }
  }

//  String interpret(Expr expression) {
//    String result;
//    try {
//      Object value = evaluate(expression);
//      result = stringify(value);
//    } catch (LoxError error) {
//      result = error.getMessage();
//    }
//    return result;
//  }

  private Object evaluate(Expr expr) {
    return expr.accept(this);
  }

  @Override
  public Object visitBinaryExpr(Expr.Binary expr) {
    Object left = evaluate(expr.left);
    Object right = evaluate(expr.right);
    checkNumberOperands(expr.operator, left, right);

    switch (expr.operator.type) {
      case PLUS:
        return (double) left + (double) right;
      case MINUS:
        return (double) left - (double) right;
      case SLASH:
        return (double) left / (double) right;
      case STAR:
        return (double) left * (double) right;
      default:
        throw new InternalError();
    }
  }

  @Override
  public Object visitGroupingExpr(Expr.Grouping expr) {
    return evaluate(expr.expression);
  }

  @Override
  public Object visitLiteralExpr(Expr.Literal expr) {
    return expr.value;
  }

  @Override
  public Object visitUnaryExpr(Expr.Unary expr) {
    Object right = evaluate(expr.right);
    checkNumberOperand(expr.operator, right);
    switch (expr.operator.type) {
      case MINUS:
        return -(double) right;
      case PLUS:
        return +(double) right;
      default:
        throw new InternalError();
    }
  }

  @Override
  public Object visitVariableExpr(Expr.Variable expr) {
    return environment.getOrDefault(expr.name.key, 0.0);
  }

  private void checkNumberOperand(Token operator, Object operand) {
    if (operand instanceof Double) return;
    throw new RuntimeError(operator.column, "Operand must be a number.");
  }

  private void checkNumberOperands(Token operator, Object left, Object right) {
    if (left instanceof Double && right instanceof Double) return;
    throw new RuntimeError(operator.column, "Operands must be numbers.");
  }

}
