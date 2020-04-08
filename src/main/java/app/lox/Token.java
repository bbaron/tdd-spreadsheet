package app.lox;

import app.impl.Key;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
final class Token {
  final TokenType type;
  final String lexeme;
  final Object literal;
  final int column;
  final Key key;

  Token(TokenType type, String lexeme, Object literal, int column, Key key) {
    this.type = type;
    this.lexeme = lexeme;
    this.literal = literal;
    this.column = column;
    this.key = key;
  }

  Token(TokenType type, String lexeme, Object literal, int column) {
    this(type, lexeme, literal, column, null);
  }

  public String toString() {
    return String.format("Token(type = %s, lexeme = '%s', literal = %s, column = %s, key = %s",
        type, lexeme, literal, column, key);
  }
}       