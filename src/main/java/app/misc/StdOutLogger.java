package app.misc;

import org.springframework.boot.convert.DurationUnit;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class StdOutLogger extends SheetLogger {
  private final String name;

  public StdOutLogger(Verbosity verbosity, String name) {
    super(verbosity);
    this.name = name;
  }

  public StdOutLogger(Verbosity verbosity, Class<?> classs) {
    this(verbosity, classs.getSimpleName());
  }

  @Override
  protected void logUnconditionally(Verbosity verbosity, Throwable t, Object message, Object... args) {
    Object formatted = message instanceof String ? String.format(message.toString(), args)
        : message == null ? "" : message;
    var now = LocalDateTime.now()
        .truncatedTo(ChronoUnit.SECONDS)
        .toString();
    System.out.printf("%s %-6s %-10s %s%n", now, verbosity, name, formatted);
    if (t != null) {
      t.printStackTrace(System.out);
    }
  }
}
