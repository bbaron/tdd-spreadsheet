package app.impl;

public record Token(TokenType type, String lexeme, Object literal, int column, Key key) {

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