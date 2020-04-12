package app.impl;

import app.misc.SheetLogger;
import app.exceptions.ParseError;
import app.misc.StdOutLogger;

import java.util.ArrayList;
import java.util.List;

import static app.impl.TokenType.COMMA;
import static app.impl.TokenType.RIGHT_PAREN;
import static app.misc.SheetLogger.Verbosity.DEBUG;
import static app.impl.TokenType.EOF;
import static app.impl.TokenType.IDENTIFIER;
import static app.impl.TokenType.LEFT_PAREN;
import static app.impl.TokenType.MINUS;
import static app.impl.TokenType.NUMBER;
import static app.impl.TokenType.PLUS;
import static app.impl.TokenType.SLASH;
import static app.impl.TokenType.STAR;

class Parser {

  private final List<Token> tokens;
  private final Key key;
  private int current = 0;
  private final SheetLogger logger = new StdOutLogger(DEBUG, getClass());
  private final References references;

  Parser(List<Token> tokens, Key key, References references) {
    this.tokens = tokens;
    this.key = key;
    this.references = references;
  }

  Expr parse() {
    Expr expr = expression();
    if (!isAtEnd()) {
      error(peek(), "expected end of expression");
    }
    return expr;
  }

  private Expr expression() {
    return addition();
  }

  private Expr addition() {
    Expr expr = multiplication();

    while (match(MINUS, PLUS)) {
      Token operator = previous();
      Expr right = multiplication();
      expr = new Expr.Binary(expr, operator, right);
    }

    return expr;
  }

  private Expr multiplication() {
    Expr expr = unary();

    while (match(SLASH, STAR)) {
      Token operator = previous();
      Expr right = unary();
      expr = new Expr.Binary(expr, operator, right);
    }

    return expr;
  }

  private Expr unary() {
    if (match(MINUS, PLUS)) {
      Token operator = previous();
      Expr right = unary();
      return new Expr.Unary(operator, right);
    }

    return call();
  }

  private Expr call() {
    Expr expr = primary();
    while (match(LEFT_PAREN)) {
      expr = finishCall(expr);
    }
    return expr;
  }

  private Expr finishCall(Expr callee) {
    List<Expr> arguments = new ArrayList<>();
    if (!check(RIGHT_PAREN)) {
      do {
        if (arguments.size() >= 50) {
          error(peek(), "Cannot have more than 255 arguments.");
        }
        arguments.add(expression());
      } while (match(COMMA));
    }

    Token paren = consumeEndOfGroup();
    return new Expr.Call(callee, paren, arguments);

  }

  private Expr primary() {
    if (match(NUMBER)) {
      return new Expr.Literal(previous().literal);
    }

    if (match(IDENTIFIER)) {
      var variable = new Expr.Variable(previous());
      logger.debug("%s is referencing %s", key, variable.name.key);
      references.addDependsOn(key, variable.name.key);
      return variable;
    }

    if (match(LEFT_PAREN)) {
      Expr expr = expression();
      consumeEndOfGroup();
      return new Expr.Grouping(expr);
    }
    throw error(peek(), "Expect expression.");
  }

  private boolean match(TokenType... types) {
    for (TokenType type : types) {
      if (check(type)) {
        advance();
        return true;
      }
    }

    return false;
  }

  private Token consumeEndOfGroup() {
    if (check(TokenType.RIGHT_PAREN)) {
      return advance();
    }
    throw error(peek(), "Expect ')' after expression.");
  }

  private boolean check(TokenType type) {
    if (isAtEnd()) return false;
    return peek().type == type;
  }

  private Token advance() {
    if (!isAtEnd()) current++;
    return previous();
  }

  private boolean isAtEnd() {
    return peek().type == EOF;
  }

  private Token peek() {
    return tokens.get(current);
  }

  private Token previous() {
    return tokens.get(current - 1);
  }

  private ParseError error(Token token, String message) {
    throw new ParseError(message, token.lexeme, token.column, token.isAtEnd());
  }

}