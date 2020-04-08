package app.lox;

import java.util.List;

public class Interpreter implements Expr.Visitor<Object> {
  private final Environment environment = new Environment();

  public String interpret(String key, String formula) {
    try {
      Scanner scanner = new Scanner(formula);
      List<Token> tokens = scanner.scanTokens();
      Parser parser = new Parser(tokens);
      Expr expression = parser.parse();
      String value = stringify(evaluate(expression));
      environment.define(key, value);
      return value;
    } catch (LoxError e) {
      System.out.println(e.getMessage());
      return "#Error";
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
    return environment.get(expr.name);
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
    if (object == null) return "";

    // Hack. Work around Java adding ".0" to integer-valued doubles.
    if (object instanceof Double) {
      String text = String.format("%.4f", object);
      int end = text.length() - 1;
      while (text.charAt(end) != '.') {
        if (text.charAt(end) != '0') break;
        end--;
      }
      text = text.substring(0, end + 1);
      if (text.charAt(text.length() - 1) == '.')
        text = text.substring(0, text.length() - 1);
      return text;
    }

    return object.toString();
  }

}
