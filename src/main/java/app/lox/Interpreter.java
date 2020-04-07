package app.lox;

import java.util.List;

public class Interpreter implements Expr.Visitor<Object> {

  public String interpret(String formula) {
    try {
      Scanner scanner = new Scanner(formula);
      List<Token> tokens = scanner.scanTokens();
      Parser parser = new Parser(tokens);
      Expr expression = parser.parse();
      return stringify(evaluate(expression));
    } catch (LoxError e) {
      return e.getMessage();
    }
  }

  String interpret(Expr expression) {
    String result;
    try {
      Object value = evaluate(expression);
      result = stringify(value);
    } catch (LoxError error) {
      result = error.getMessage();
    }
    return result;
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

  private Object evaluate(Expr expr) {
    return expr.accept(this);
  }

  private void checkNumberOperand(Token operator, Object operand) {
    if (operand instanceof Double) return;
    throw new RuntimeError(operator, "Operand must be a number.");
  }

  private void checkNumberOperands(Token operator, Object left, Object right) {
    if (left instanceof Double && right instanceof Double) return;
    throw new RuntimeError(operator, "Operands must be numbers.");
  }

  private String stringify(Object object) {
    if (object == null) return "nil";

    // Hack. Work around Java adding ".0" to integer-valued doubles.
    if (object instanceof Double) {
      String text = object.toString();
      if (text.endsWith(".0")) {
        text = text.substring(0, text.length() - 2);
      }
      return text;
    }

    return object.toString();
  }
}
