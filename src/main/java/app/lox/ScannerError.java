package app.lox;

public class ScannerError extends LoxError {
  public ScannerError(int column) {
    super("unexpected character at column " + column);
  }
}
