package app.impl;

import app.exceptions.KeyError;
import app.misc.Helpers;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;

import static app.misc.Helpers.*;
import static java.lang.Integer.*;

class Key {
  private static final ConcurrentMap<String, Key> keys = new ConcurrentHashMap<>();
  private final String value;
  private static final Pattern KEY_REGEX = Pattern.compile("^[A-Z]+[0-9]+$");

  private Key(String value) {
    this.value = value;
  }

  private Key(int col, int row) {
    this(String.format("%s%s", Helpers.getSpreadsheetColumnName(col), row));
  }

  static Key of(String value) {
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

  String getValue() {
    return value;
  }

  String getColumn() {
    return value.replace("\\d+", "");
  }

  Key smaller(Key that) {
    if (columnsEqual(that)) {
      return this.getRowIndex() <= that.getRowIndex() ? this : that;
    }
    if (rowsEqual(that)) {
      return this.getColumnIndex() <= that.getColumnIndex() ? this : that;
    }
    throw new IllegalArgumentException(String.format("%s and %s must have a common row or column", this, that));
  }

  boolean columnsEqual(Key that) {
    return this.getColumnIndex() == that.getColumnIndex();
  }

  boolean rowsEqual(Key that) {
    return this.getRowIndex() == that.getRowIndex();
  }

  Key other(Key k1, Key k2) {
    return this.equals(k1) ? k2 : k1;
  }

  int getColumnIndex() {
    return getSpreadsheetColumnIndex(getColumn());
  }

  int getRowIndex() {
    return parseInt(value.replace("\\D+", ""));
  }

  Key incrementRow() {
    return new Key(getColumnIndex(), getRowIndex() + 1);
  }

  Key incrementColumn() {
    return new Key(getColumnIndex() + 1, getRowIndex());
  }

  @Override
  public String toString() {
    return String.format("key[%s]", value);
  }
}
