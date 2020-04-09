package app.exceptions;

public class SheetError extends RuntimeException {
  public SheetError(String message) {
    super(message);
  }
}
