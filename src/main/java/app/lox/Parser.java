package app.lox;

import java.util.List;

import static app.lox.TokenType.EOF;
import static app.lox.TokenType.IDENTIFIER;
import static app.lox.TokenType.LEFT_PAREN;
import static app.lox.TokenType.MINUS;
import static app.lox.TokenType.NUMBER;
import static app.lox.TokenType.PLUS;
import static app.lox.TokenType.RIGHT_PAREN;
import static app.lox.TokenType.SLASH;
import static app.lox.TokenType.STAR;

class Parser {

  private final List<Token> tokens;
  private int current = 0;

  Parser(List<Token> tokens) {
    this.tokens = tokens;
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

    return primary();
  }

  private Expr primary() {
    if (match(NUMBER)) {
      return new Expr.Literal(previous().literal);
    }

    if (match(IDENTIFIER)) {
      return new Expr.Variable(previous());
    }

    if (match(LEFT_PAREN)) {
      Expr expr = expression();
      consume(RIGHT_PAREN, "Expect ')' after expression.");
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

  private Token consume(TokenType type, String message) {
    if (check(type)) return advance();

    throw error(peek(), message);
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
    throw new ParseError(token, message);
  }

  private void synchronize() {
    advance();

    while (!isAtEnd()) {
//      if (previous().type == SEMICOLON) return;
//
//      switch (peek().type) {
//        case CLASS:
//        case FUN:
//        case VAR:
//        case FOR:
//        case IF:
//        case WHILE:
//        case PRINT:
//        case RETURN:
//          return;
//      }
//
      advance();
    }
  }
}