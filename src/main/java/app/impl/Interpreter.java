package app.impl;

import app.exceptions.CircularityError;
import app.exceptions.RuntimeError;
import app.exceptions.SheetError;
import app.misc.SheetLogger;
import app.misc.StdOutLogger;

import static app.misc.SheetLogger.Verbosity.DEBUG;

class Interpreter {
  private final Environment environment;
  private final References references;
  private final SheetLogger logger = new StdOutLogger(DEBUG, getClass());

  Interpreter(Environment environment, References references) {
    this.environment = environment;
    this.references = references;
  }

  Expr interpret(Key key, String formula) {
    try {
      var scanner = new Scanner(formula);
      var tokens = scanner.scanTokens();
      var parser = new Parser(tokens, key, references);
      var expression = parser.parse();
      var value = dispatch(expression);
      environment.define(key, value);
      return expression;
    } catch (SheetError e) {
      logger.debug(e, "error in interpret");
      var message = e instanceof CircularityError ? "#Circular" : "#Error";
      environment.define(key, message);
      return null;
    }
  }

  Object dispatch(Expr expr) {
    return switch (expr) {
      case Expr.Binary e -> evaluate(e);
      case Expr.Grouping e -> evaluate(e);
      case Expr.Literal e -> evaluate(e);
      case Expr.Unary e -> evaluate(e);
      case Expr.Variable e -> evaluate(e);
    };
  }

  private Object evaluate(Expr.Binary expr) {
    var left = dispatch(expr.left());
    var right = dispatch(expr.right());
    checkNumberOperands(expr.operator(), left, right);
    var lhs = (double) left;
    var rhs = (double) right;

    return switch (expr.operator().type()) {
      case PLUS -> lhs + rhs;
      case MINUS -> lhs - rhs;
      case SLASH -> lhs / rhs;
      case STAR -> lhs * rhs;
      default -> throw new InternalError();
    };
  }

  private Object evaluate(Expr.Grouping expr) {
    return dispatch(expr.expression());
  }

  private Object evaluate(Expr.Literal expr) {
    return expr.value();
  }

  private Object evaluate(Expr.Unary expr) {
    var right = dispatch(expr.right());
    checkNumberOperand(expr.operator(), right);
    var rhs = (double) right;
    return switch (expr.operator().type()) {
      case MINUS -> -rhs;
      case PLUS -> +rhs;
      default -> throw new InternalError();
    };
  }

  private Object evaluate(Expr.Variable expr) {
    return environment.getOrDefault(expr.name().key(), 0.0);
  }

  private void checkNumberOperand(Token operator, Object operand) {
    if (operand instanceof Double) return;
    throw new RuntimeError(operator.column(), "Operand must be a number.");
  }

  private void checkNumberOperands(Token operator, Object left, Object right) {
    if (left instanceof Double && right instanceof Double) return;
    throw new RuntimeError(operator.column(), "Operands must be numbers.");
  }

}
