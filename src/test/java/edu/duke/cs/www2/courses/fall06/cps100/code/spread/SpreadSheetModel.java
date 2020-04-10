package edu.duke.cs.www2.courses.fall06.cps100.code.spread;

import javax.swing.table.AbstractTableModel;

/**
 * This class specifies the data format
 * for the SpreadSheet JTable
 *
 * @author Thierry Manfé
 * @version 1.0 July-2002
 */
class SpreadSheetModel extends AbstractTableModel {

  final private SpreadSheet _dpyTable;

  static Interpreter interpreter;

  private int _nbRow;
  private int _nbColumn;

  protected SheetCell[][] cells;

  /**
   * Create a nbRow by nbColumn SpreadSheetModel.
   *
   * @param cells The cell array
   * @param table The associated SpreadSheet
   */
  SpreadSheetModel(SheetCell[][] cells, SpreadSheet table) {
    _dpyTable = table;
    _nbRow = cells.length;
    _nbColumn = cells[0].length;
    this.cells = cells;
    interpreter = new Interpreter(this);
  }

  @SuppressWarnings("unused")
  private void clean() {
    _nbRow = _nbColumn = 0;
    cells = null;
  }

  public int getRowCount() {
    return _nbRow;
  }

  public int getColumnCount() {
    return _nbColumn;
  }

  public boolean isCellEditable(int row, int col) {
    return true;
  }

  public Object getValueAt(int row, int column) {
    Object value = cells[row][column];
    if (value != null && value.toString() != null) {
      System.out.printf("getValue %s at row=%s, col=%s%n", value, row, column);
    }
    return value;
  }

  /**
   * Mark the corresponding cell
   * as being edited. Its formula
   * must be displayed instead of
   * its result
   */
  void setEditMode(int row, int column) {
    cells[row][column].state = SheetCell.EDITED;
  }

  void setDisplayMode(int row, int column) {
    cells[row][column].state = SheetCell.UPDATED;
  }

  public void setValueAt(Object value, int row, int column) {
    if (value != null && value.toString() != null) {
      System.out.printf("setValue %s at row=%s, col=%s%n", value, row, column);
    }
    cells[row][column].formula = (String) value;
    cells[row][column].userUpdate();
    _dpyTable.repaint();

  }

}

 