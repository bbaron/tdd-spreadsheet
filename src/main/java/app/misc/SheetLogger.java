package app.misc;

import java.util.function.Supplier;

import static app.misc.SheetLogger.Verbosity.DEBUG;
import static app.misc.SheetLogger.Verbosity.ERROR;
import static app.misc.SheetLogger.Verbosity.INFO;
import static app.misc.SheetLogger.Verbosity.WARN;
import static java.util.Objects.requireNonNull;

@SuppressWarnings("unused")
public abstract class SheetLogger {
  private final Verbosity verbosity;

  protected SheetLogger(Verbosity verbosity) {
    this.verbosity = verbosity;
  }

  public enum Verbosity {
    DEBUG(3), INFO(2), WARN(1), ERROR(0);
    private final int level;

    Verbosity(int level) {
      this.level = level;
    }

    public static Verbosity of(int level) {
      if (level > INFO.level) return DEBUG;
      if (level > WARN.level) return INFO;
      if (level > ERROR.level) return WARN;
      return ERROR;
    }
  }

  public final int level() {
    return verbosity().level;
  }

  public final Verbosity verbosity() {
    return verbosity;
  }

  public final boolean isDebug() {
    return level() > INFO.level;
  }

  public final boolean isInfo() {
    return level() > WARN.level;
  }

  public final boolean isWarn() {
    return level() > ERROR.level;
  }

  public final boolean isError() {
    return true;
  }

  protected abstract void logUnconditionally(Verbosity verbosity, Throwable t, Object message, Object... args);

  public final void log(Verbosity verbosity, Throwable t, Supplier<?> messageSupplier) {
    if (requireNonNull(verbosity).level >= level()) {
      logUnconditionally(verbosity, t, messageSupplier.get());
    }
  }

  public final void log(Verbosity verbosity, Supplier<?> messageSupplier) {
    log(verbosity, null, messageSupplier);
  }

  public final void log(Verbosity verbosity, Throwable t, Object message, Object... args) {
    if (requireNonNull(verbosity).level >= level()) {
      logUnconditionally(verbosity, t, message, args);
    }
  }

  public final void log(Verbosity verbosity, Object message, Object... args) {
    log(verbosity, null, message, args);
  }

  public final void error(Throwable t, Object message, Object... args) {
    log(ERROR, t, message, args);
  }

  public final void error(Object message, Object... args) {
    error(null, message, args);
  }

  public final void warn(Throwable t, Object message, Object... args) {
    log(WARN, t, message, args);
  }

  public final void warn(Object message, Object... args) {
    warn(null, message, args);
  }

  public final void info(Throwable t, Object message, Object... args) {
    log(INFO, t, message, args);
  }

  public final void info(Object message, Object... args) {
    info(null, message, args);
  }

  public final void debug(Throwable t, Object message, Object... args) {
    log(DEBUG, t, message, args);
  }

  public final void debug(Object message, Object... args) {
    debug(null, message, args);
  }


}
