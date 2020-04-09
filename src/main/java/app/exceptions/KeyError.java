package app.exceptions;

import app.exceptions.SheetError;

public class KeyError extends SheetError {
  public KeyError(String message) {
    super(message);
  }
}
