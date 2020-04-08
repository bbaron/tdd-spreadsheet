package app.impl;

import app.Sheet;

public class SheetImpl implements Sheet {
  private final Grid grid = new Grid();

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

