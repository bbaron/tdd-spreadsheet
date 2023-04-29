package app.exceptions;

import app.impl.Token;

public class ParseError extends LoxError {

  public ParseError(String message, Token token) {
    this(message, token.lexeme(), token.column(), token.isAtEnd());
  }

  public ParseError(String message, String lexeme, int column, boolean atEnd) {
    super(error(atEnd, message, lexeme, column));
  }

  private static String error(boolean atEnd, String message, String lexeme, int column) {
    if (atEnd) {
      return report(column, " at end", message);
    } else {
      return report(column, " at '%s'".formatted(lexeme), message);
    }
  }
  private static String report(int column, String where, String message) {
    return "[column %d] Error%s: %s".formatted(column, where, message);
  }

}
