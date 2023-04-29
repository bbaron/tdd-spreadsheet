package app.impl;

import app.exceptions.CircularityError;
import app.exceptions.RuntimeError;
import app.exceptions.SheetError;
import app.misc.SheetLogger;
import app.misc.StdOutLogger;

import static app.misc.SheetLogger.Verbosity.DEBUG;

class Interpreter implements Expr.Visitor<Object> {
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
      var value = evaluate(expression);
      environment.define(key, value);
      return expression;
    } catch (SheetError e) {
      logger.debug(e, "error in interpret");
      var message = e instanceof CircularityError ? "#Circular" : "#Error";
      environment.define(key, message);
      return null;
    }
  }

  Object evaluate(Expr expr) {
    return expr.accept(this);
  }

  @Override
  public Object visit(Expr.Binary expr) {
    var left = evaluate(expr.left());
    var right = evaluate(expr.right());
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

  @Override
  public Object visit(Expr.Grouping expr) {
    return evaluate(expr.expression());
  }

  @Override
  public Object visit(Expr.Literal expr) {
    return expr.value();
  }

  @Override
  public Object visit(Expr.Unary expr) {
    var right = evaluate(expr.right());
    checkNumberOperand(expr.operator(), right);
    var rhs = (double) right;
    return switch (expr.operator().type()) {
      case MINUS -> -rhs;
      case PLUS -> +rhs;
      default -> throw new InternalError();
    };
  }

  @Override
  public Object visit(Expr.Variable expr) {
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
