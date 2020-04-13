package app;

class TableModelCellChangeNotifier implements CellChangeNotifier {
  SheetTableModel sheetTableModel;

  public TableModelCellChangeNotifier() {
  }

  @Override
  public void cellChangedAt(int row, int col) {

//    System.out.printf("firing at %s %s%n", row, col);
    sheetTableModel.fireTableCellUpdated(row, col);
  }
}
