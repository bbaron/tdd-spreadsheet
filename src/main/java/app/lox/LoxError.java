package app.lox;

public class LoxError extends RuntimeException {
  public LoxError(String message) {
    super(message);
  }
}
