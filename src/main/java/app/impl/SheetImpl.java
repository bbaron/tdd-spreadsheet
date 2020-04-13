package app.impl;

import app.CellChangeNotifier;
import app.Sheet;
import app.SheetUI;

import javax.swing.table.AbstractTableModel;

import static app.misc.Helpers.getSpreadsheetColumnName;

public class SheetImpl extends AbstractTableModel implements Sheet {
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

  @Override
  public int getRowCount() {
    return SheetUI.ROW_COUNT;
  }

  @Override
  public int getColumnCount() {
    return SheetUI.COL_COUNT;
  }

  @Override
  public Object getValueAt(int rowIndex, int columnIndex) {
    rowColCheck(rowIndex, columnIndex);
    if (columnIndex == 0) return "" + (rowIndex + 1);
    String key = makeKey(rowIndex, columnIndex);
    return get(key);
  }

  @Override
  public String getLiteralValueAt(int rowIndex, int columnIndex) {
    rowColCheck(rowIndex, columnIndex);
    String key = makeKey(rowIndex, columnIndex);
    return getLiteral(key);
  }
  @Override
  public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
    rowColCheck(rowIndex, columnIndex);
    if (columnIndex == 0) {
      throw new IllegalArgumentException("column 0 is read-only");
    }
    String key = makeKey(rowIndex, columnIndex);
    put(key, aValue.toString());
  }

  @Override
  public String getColumnName(int column) {
    return getSpreadsheetColumnName(column);
  }

  private String makeKey(int rowIndex, int columnIndex) {
    return getColumnName(columnIndex) + (rowIndex + 1);
  }

  private void rowColCheck(int rowIndex, int columnIndex) {
    if (rowIndex < 0 || rowIndex >= getRowCount() || columnIndex < 0 || columnIndex >= getColumnCount()) {
      throw new IllegalArgumentException(String.format("row %s, col %s out of bounds", rowIndex, columnIndex));
    }
  }

}

