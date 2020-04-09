package app.impl;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
final class Token {
  public final TokenType type;
  public final String lexeme;
  public final Object literal;
  public final int column;
  public final Key key;

  public Token(TokenType type, String lexeme, Object literal, int column, Key key) {
    this.type = type;
    this.lexeme = lexeme;
    this.literal = literal;
    this.column = column;
    this.key = key;
  }

  public Token(TokenType type, String lexeme, Object literal, int column) {
    this(type, lexeme, literal, column, null);
  }

  public String toString() {
    return String.format("Token(type = %s, lexeme = '%s', literal = %s, column = %s, key = %s",
        type, lexeme, literal, column, key);
  }

  public boolean isAtEnd() {
    return type == TokenType.EOF;
  }
}