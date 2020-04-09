package app.impl;

import app.exceptions.ScannerError;

import java.util.ArrayList;
import java.util.List;

import static app.impl.TokenType.EOF;
import static app.impl.TokenType.IDENTIFIER;
import static app.impl.TokenType.LEFT_PAREN;
import static app.impl.TokenType.MINUS;
import static app.impl.TokenType.NUMBER;
import static app.impl.TokenType.PLUS;
import static app.impl.TokenType.RIGHT_PAREN;
import static app.impl.TokenType.SLASH;
import static app.impl.TokenType.STAR;

class Scanner {
  private final String source;
  private final List<Token> tokens = new ArrayList<>();
  private int start = 0;
  private int current = 0;
  private int column = 0;

  Scanner(String source) {
    this.source = source;
  }

  List<Token> scanTokens() {
    while (!isAtEnd()) {
      // We are at the beginning of the next lexeme.
      start = current;
      scanToken();
    }

    tokens.add(new Token(EOF, "", null, column));
    return tokens;
  }

  private void scanToken() {
    char c = advance();
    switch (c) {
      case '(':
        addToken(LEFT_PAREN);
        break;
      case ')':
        addToken(RIGHT_PAREN);
        break;
      case '-':
        addToken(MINUS);
        break;
      case '+':
        addToken(PLUS);
        break;
      case '*':
        addToken(STAR);
        break;
      case '/':
        addToken(SLASH);
        break;
      case ' ':
      case '\r':
      case '\t':
        // Ignore whitespace.
        break;

      case '\n':
        column = 0;
        break;
      default:
        if (c == '.' || isDigit(c)) {
          number();
        } else if (isAlpha(c)) {
          identifier();
        } else {
          throw new ScannerError(column);
        }
        break;
    }
  }

  private void identifier() {
    while (isAlpha(peek())) advance();
    while (isDigit(peek())) advance();
    addToken(IDENTIFIER);
  }

  private void number() {
    while (isDigit(peek())) advance();

    // Look for a fractional part.
    if (peek() == '.' && isDigit(peekNext())) {
      // Consume the "."
      advance();

      while (isDigit(peek())) advance();
    }

    addToken(NUMBER,
        Double.parseDouble(source.substring(start, current)));
  }

  private boolean isDigit(char c) {
    return c >= '0' && c <= '9';
  }

  private char peek() {
    if (isAtEnd()) return '\0';
    return source.charAt(current);
  }

  private char peekNext() {
    if (current + 1 >= source.length()) return '\0';
    return source.charAt(current + 1);
  }

  private boolean isAlpha(char c) {
    return (c >= 'a' && c <= 'z') ||
        (c >= 'A' && c <= 'Z') ||
        c == '_';
  }

  private boolean isAtEnd() {
    return current >= source.length();
  }

  private char advance() {
    current++;
    column++;
    return source.charAt(current - 1);
  }

  private void addToken(TokenType type) {
    addToken(type, null);
  }

  private void addToken(TokenType type, Object literal) {
    String text = source.substring(start, current);

    Key key = type == IDENTIFIER ? Key.of(text) : null;
    tokens.add(new Token(type, text, literal, column, key));
  }
} 