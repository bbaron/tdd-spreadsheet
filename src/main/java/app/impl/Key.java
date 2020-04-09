package app.impl;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;

public class Key {
  private static final ConcurrentMap<String, Key> keys = new ConcurrentHashMap<>();
  private final String value;
  private static final Pattern KEY_REGEX = Pattern.compile("^[A-Z]+[0-9]+$");

  private Key(String value) {
    this.value = value;
  }

  public static Key of(String value) {
    String norm = validate(value);
    return keys.computeIfAbsent(norm, Key::new);
  }

  private static String normalize(String key) {
    return key.toUpperCase();
  }

  private static String validate(String raw) {
    if (raw == null) throw new KeyError("null key");
    String key = normalize(raw);
    if (!KEY_REGEX.matcher(key).matches()) {
      throw new KeyError("invalid key: " + raw);
    }
    return key;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(value);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!(obj instanceof Key)) return false;
    Key that = (Key) obj;
    return this.value.equals(that.value);
  }

  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return String.format("key[%s]", value);
  }
}
