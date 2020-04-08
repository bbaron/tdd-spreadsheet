package app.impl;

import app.SheetLogger;

import java.time.LocalDateTime;

public class StdOutLogger extends SheetLogger {
  public StdOutLogger(Verbosity verbosity) {
    super(verbosity);
  }

  public StdOutLogger() {
    this(Verbosity.INFO);
  }

  @Override
  protected void logUnconditionally(Verbosity verbosity, Throwable t, Object message, Object... args) {
    Object formatted = message instanceof String ? String.format(message.toString(), args)
        : message == null ? "" : message;
    var now = LocalDateTime.now().toString();
    System.out.printf("%s %-6s %s%n", now, verbosity, formatted);
    if (t != null) {
      t.printStackTrace(System.out);
    }
  }
}
