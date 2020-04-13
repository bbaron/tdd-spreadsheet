package app;

import javax.swing.table.AbstractTableModel;

class TableModelCellChangeNotifier implements CellChangeNotifier {
  AbstractTableModel sheetTableModel;

  public TableModelCellChangeNotifier() {
  }

  @Override
  public void cellChangedAt(int row, int col) {

//    System.out.printf("firing at %s %s%n", row, col);
    sheetTableModel.fireTableCellUpdated(row, col);
  }
}
