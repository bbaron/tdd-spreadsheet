package app.exceptions;

public class ParseError extends LoxError {

  public ParseError(String message, String lexeme, int column, boolean atEnd) {
    super(error(atEnd, message, lexeme, column));
  }

  private static String error(boolean atEnd, String message, String lexeme, int column) {
    if (atEnd) {
      return report(column, " at end", message);
    } else {
      return report(column, " at '" + lexeme + "'", message);
    }
  }
  private static String report(int column, String where, String message) {
    return "[column " + column + "] Error" + where + ": " + message;
  }

}
