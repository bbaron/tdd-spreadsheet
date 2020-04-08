package app.impl;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static java.util.Objects.requireNonNull;

public class Key {
  private static final ConcurrentMap<String, Key> keys = new ConcurrentHashMap<>();
  private final String value;

  private Key(String value) {
    this.value = value;
  }

  public static Key of(String value) {
    String norm = requireNonNull(normalize(value));
    return keys.computeIfAbsent(norm, Key::new);
  }

  private static String normalize(String key) {
    return key.toUpperCase();
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

  @Override
  public String toString() {
    return String.format("key[%s]", value);
  }
}
