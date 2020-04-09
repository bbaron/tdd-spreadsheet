package app.exceptions;

public class RuntimeError extends LoxError {
//  public final Token token;

  public RuntimeError(int column, String message) {
    super(message + " [column " + column + "]");
  }
}