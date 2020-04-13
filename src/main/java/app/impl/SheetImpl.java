package app.impl;

import app.CellChangeNotifier;
import app.Sheet;

public class SheetImpl implements Sheet {
  private final Grid grid;

  public SheetImpl(CellChangeNotifier notifier) {
    this.grid = new Grid(notifier);
  }

  @Override
  public String get(String key) {
    return grid.get(key);
  }

  @Override
  public void put(String key, String literal) {
    grid.put(key, literal);
  }

  @Override
  public String getLiteral(String key) {
    return grid.getLiteral(key);
  }
}

