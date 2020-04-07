package app.lox;

class ParseError extends LoxError {
  final Token token;

  ParseError(Token token, String message) {
    super(error(token, message));
    this.token = token;
  }

  private static String error(Token token, String message) {
    if (token.type == TokenType.EOF) {
      return report(token.column, " at end", message);
    } else {
      return report(token.column, " at '" + token.lexeme + "'", message);
    }
  }
  private static String report(int column, String where, String message) {
    return "[column " + column + "] Error" + where + ": " + message;
  }

}
