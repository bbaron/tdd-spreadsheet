package app.lox;

class RuntimeError extends LoxError {
  final Token token;

  RuntimeError(Token token, String message) {
    super(message + " [column " + token.column + "]");
    this.token = token;
  }
}