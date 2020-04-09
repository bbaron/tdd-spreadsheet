package app;

import app.misc.SheetLogger;
import app.misc.StdOutLogger;

import javax.swing.table.AbstractTableModel;

import static app.misc.SheetLogger.Verbosity.DEBUG;

public class SheetTableModel extends AbstractTableModel {
  private static final int COL_COUNT = 50;
  private static final int ROW_COUNT = 100;
  private final Sheet sheet;
  private final SheetLogger logger = new StdOutLogger(DEBUG, getClass());

  public SheetTableModel(Sheet sheet) {
    this.sheet = sheet;
  }

  @Override
  public int getRowCount() {
    return ROW_COUNT;
  }

  @Override
  public int getColumnCount() {
    return COL_COUNT;
  }

  @Override
  public Object getValueAt(int rowIndex, int columnIndex) {
    rowColCheck(rowIndex, columnIndex);
    if (columnIndex == 0) return "" + (rowIndex + 1);
    String key = makeKey(rowIndex, columnIndex);
    return sheet.get(key);
  }

  public String getLiteralValueAt(int rowIndex, int columnIndex) {
    rowColCheck(rowIndex, columnIndex);
    String key = makeKey(rowIndex, columnIndex);
    return sheet.getLiteral(key);
  }

  @Override
  public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
    rowColCheck(rowIndex, columnIndex);
    if (columnIndex == 0) {
      throw new IllegalArgumentException("column 0 is read-only");
    }
    String key = makeKey(rowIndex, columnIndex);
    sheet.put(key, aValue.toString());
    super.fireTableCellUpdated(rowIndex, columnIndex);
  }

  @Override
  public String getColumnName(int column) {
    return column == 0 ? "" : super.getColumnName(column - 1);
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
