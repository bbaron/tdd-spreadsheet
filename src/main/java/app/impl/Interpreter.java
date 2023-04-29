package app.impl;

import app.exceptions.CircularityError;
import app.exceptions.RuntimeError;
import app.exceptions.SheetError;
import app.misc.SheetLogger;
import app.misc.StdOutLogger;
import lombok.RequiredArgsConstructor;

import static app.misc.SheetLogger.Verbosity.DEBUG;

@RequiredArgsConstructor
class Interpreter {
  private final Environment environment;
  private final References references;
  private final SheetLogger logger;

  Interpreter(Environment environment, References references) {
    this(environment, references, new StdOutLogger(DEBUG, Interpreter.class));
  }

  Expr interpret(Key key, String formula) {
    try {
      var tokens = Scanner.apply(formula);
      var expression = Parser.apply(tokens, key, references);
      var value = apply(expression);
      environment.define(key, value);
      return expression;
    } catch (SheetError e) {
      logger.debug(e, "error in interpret");
      var message = e instanceof CircularityError ? "#Circular" : "#Error";
      environment.define(key, message);
      return null;
    }
  }

  Object apply(Expr expr) {
    return switch (expr) {
      case Expr.Binary binary -> evaluateBinary(binary);
      case Expr.Grouping grouping -> evaluateGrouping(grouping);
      case Expr.Literal literal -> evaluateLiteral(literal);
      case Expr.Unary unary -> evaluateUnary(unary);
      case Expr.Variable variable -> evaluateVariable(variable);
    };
  }

  private double evaluateBinary(Expr.Binary binary) {
    var left = apply(binary.left());
    var right = apply(binary.right());
    var operands = Operands.of(binary.operator(), left, right);

    return switch (binary.operator().type()) {
      case PLUS -> operands.add();
      case MINUS -> operands.sub();
      case SLASH -> operands.div();
      case STAR -> operands.mul();
      default -> throw new InternalError();
    };
  }

  private Object evaluateGrouping(Expr.Grouping grouping) {
    return apply(grouping.expression());
  }

  private double evaluateLiteral(Expr.Literal literal) {
    return literal.value();
  }

  private double evaluateUnary(Expr.Unary expr) {
    var right = apply(expr.right());
    var operand = Operand.of(expr.operator(), right);
    return switch (expr.operator().type()) {
      case MINUS -> operand.minus();
      case PLUS -> operand.plus();
      default -> throw new InternalError();
    };
  }

  private Object evaluateVariable(Expr.Variable variable) {
    return environment.getOrDefault(variable.name().key(), 0.0);
  }

  record Operand(double operand) {
    double minus() {
      return -operand;
    }

    double plus() {
      return +operand;
    }

    static Operand of(Token operator, Object object) {
      if (object instanceof Double operand) return new Operand(operand);
      throw new RuntimeError(operator.column(), "Operand must be a number.");
    }
  }

  record Operands(double left, double right) {
    double add() {
      return left + right;
    }

    double sub() {
      return left - right;
    }

    double mul() {
      return left * right;
    }

    double div() {
      return left / right;
    }
    static Operands of(Token operator, Object a, Object b) {
      if (a instanceof Double left && b instanceof Double right) return new Operands(left, right);
      throw new RuntimeError(operator.column(), "Operands must be numbers.");
    }
  }

}
