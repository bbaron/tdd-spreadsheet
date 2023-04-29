package app.swing;/*
 * TableDemo.java requires no other files.
 */

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import java.awt.*;

import static app.swing.TableDemos.COLUMN_NAMES;
import static app.swing.TableDemos.DATA;

/**
 * TableDemo is just like SimpleTableDemo, except that it
 * uses a custom TableModel.
 */
public class TableDemo extends JPanel {

  public TableDemo() {
    super(new GridLayout(1, 0));

    JTable table = new JTable(new MyTableModel());
    table.getModel().addTableModelListener(this::tableChanged);
    table.setPreferredScrollableViewportSize(new Dimension(500, 70));
    table.setFillsViewportHeight(true);

    //Create the scroll pane and add the table to it.
    JScrollPane scrollPane = new JScrollPane(table);

    //Add the scroll pane to this panel.
    add(scrollPane);
  }

  private void tableChanged(TableModelEvent e) {
    int row = e.getFirstRow();
    int col = e.getColumn();
    var model = (TableModel) e.getSource();
    String name = model.getColumnName(col);
    Object data = model.getValueAt(row, col);
    System.out.printf("tableChanged name %s row %s col %s data %s%n",
        name, row, col, data);
  }

  static class MyTableModel extends AbstractTableModel {
    private final String[] columnNames = COLUMN_NAMES;
    private final Object[][] data = DATA;

    public int getColumnCount() {
      return columnNames.length;
    }

    public int getRowCount() {
      return data.length;
    }

    public String getColumnName(int col) {
//      return columnNames[col];
      return super.getColumnName(col);
    }

    public Object getValueAt(int row, int col) {
      return data[row][col];
    }

    /*
     * JTable uses this method to determine the default renderer/
     * editor for each cell.  If we didn't implement this method,
     * then the last column would contain text ("true"/"false"),
     * rather than a check box.
     */
    public Class<?> getColumnClass(int c) {
      return getValueAt(0, c).getClass();
    }

    /*
     * Don't need to implement this method unless your table's
     * editable.
     */
    public boolean isCellEditable(int row, int col) {
      //Note that the data/cell address is constant,
      //no matter where the cell appears onscreen.
      return col >= 2;
    }

    /*
     * Don't need to implement this method unless your table's
     * data can change.
     */
    public void setValueAt(Object value, int row, int col) {
      System.out.println("Setting value at " + row + "," + col
          + " to " + value
          + " (an instance of "
          + value.getClass() + ")");

      data[row][col] = value;
      fireTableCellUpdated(row, col);

      System.out.println("New value of data:");
      printDebugData();
    }

    private void printDebugData() {
      int numRows = getRowCount();
      int numCols = getColumnCount();

      for (int i = 0; i < numRows; i++) {
        System.out.print("    row " + i + ":");
        for (int j = 0; j < numCols; j++) {
          System.out.print("  " + data[i][j]);
        }
        System.out.println();
      }
      System.out.println("--------------------------");
    }
  }

  /**
   * Create the GUI and show it.  For thread safety,
   * this method should be invoked from the
   * event-dispatching thread.
   */
  private static void createAndShowGUI() {
    //Create and set up the window.
    JFrame frame = new JFrame("TableDemo");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    //Create and set up the content pane.
    TableDemo newContentPane = new TableDemo();
    newContentPane.setOpaque(true); //content panes must be opaque
    frame.setContentPane(newContentPane);

    //Display the window.
    frame.pack();
    frame.setVisible(true);
  }

  public static void main(String[] args) {
    //Schedule a job for the event-dispatching thread:
    //creating and showing this application's GUI.
    javax.swing.SwingUtilities.invokeLater(TableDemo::createAndShowGUI);
  }
}