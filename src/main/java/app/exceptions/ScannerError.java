package app.exceptions;

public class ScannerError extends LoxError {
  public ScannerError(int column) {
    super("unexpected character at column " + column);
  }
}
