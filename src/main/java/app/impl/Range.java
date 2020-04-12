package app.impl;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;

class Range implements Iterable<Key> {
  private final Key from, to;
  private final boolean rowMode;

  private Range(Key a, Key b) {
    rowMode = a.rowsEqual(b);
    from = a.smaller(b);
    to = increment(from.other(a, b));
  }

  static Optional<Range> of(Key a, Key b) {
    if (!a.columnsEqual(b) && !a.rowsEqual(b)) {
      return Optional.empty();
    }
    return Optional.of(new Range(a, b));
  }

  private Key increment(Key k) {
    return rowMode ? k.incrementRow() : k.incrementColumn();
  }

  @Override
  public Iterator<Key> iterator() {
    return new Itr();
  }

  private class Itr implements Iterator<Key> {
    Key cursor = from;

    @Override
    public boolean hasNext() {
      return !cursor.equals(to);
    }

    @Override
    public Key next() {
      if (!hasNext()) throw new NoSuchElementException();
      cursor = increment(cursor);
      return cursor;
    }
  }
}
