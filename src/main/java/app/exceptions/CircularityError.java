package app.exceptions;

public class CircularityError extends LoxError {
  public CircularityError(String message) {
    super(message);
  }
}
