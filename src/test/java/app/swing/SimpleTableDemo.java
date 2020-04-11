package app.swing;/*
 * SimpleTableDemo.java requires no other files.
 */

import app.misc.Helpers;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import static app.swing.TableDemos.*;
import static javax.swing.SwingUtilities.invokeLater;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class SimpleTableDemo extends JPanel {

  public SimpleTableDemo() {
    super(new GridLayout(1, 0));

//    final JTable table = new JTable(DATA, COLUMN_NAMES);
    final JTable table = new JTable(new AbstractTableModel() {
      @Override
      public int getRowCount() {
        return DATA.length;
      }

      @Override
      public int getColumnCount() {
        return DATA[0].length;
      }

      @Override
      public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex == 0) return (rowIndex + 1) + "";
        return DATA[rowIndex][columnIndex];
      }

      @Override
      public String getColumnName(int column) {
        return Helpers.getSpreadsheetColumnName(column);
      }
    });
//    var model = table.getModel();
//    for (int i = 0; i < DATA.length; i++) {
//      var row = DATA[i];
//      for (int j = 0; j < row.length; j++) {
//        model.setValueAt(row[j], i, j);
//      }
//    }

//    table.setPreferredScrollableViewportSize(new Dimension(500, 140));
//    table.setFillsViewportHeight(true);

    table.addMouseListener((MouseClicker) e -> printDebugData(table));

    //Create the scroll pane and add the table to it.
    JScrollPane scrollPane = new JScrollPane(table);

    //Add the scroll pane to this panel.
    add(table);
  }

  private void printDebugData(JTable table) {
    int numRows = table.getRowCount();
    int numCols = table.getColumnCount();
    javax.swing.table.TableModel model = table.getModel();

    System.out.println("Value of data: ");
    for (int i = 0; i < numRows; i++) {
      System.out.print("    row " + i + ":");
      for (int j = 0; j < numCols; j++) {
        System.out.print("  " + model.getValueAt(i, j));
      }
      System.out.println();
    }
    System.out.println("--------------------------");
  }

  /**
   * Create the GUI and show it.  For thread safety,
   * this method should be invoked from the
   * event-dispatching thread.
   */
  private static void createAndShowGUI() {
    //Create and set up the window.
    JFrame frame = new JFrame("SimpleTableDemo");
    frame.setDefaultCloseOperation(EXIT_ON_CLOSE);

    //Create and set up the content pane.
    SimpleTableDemo newContentPane = new SimpleTableDemo();
    newContentPane.setOpaque(true); //content panes must be opaque
    frame.setContentPane(newContentPane);

    //Display the window.
    frame.pack();
    frame.setVisible(true);
  }

  public static void main(String[] args) {
    //Schedule a job for the event-dispatching thread:
    //creating and showing this application's GUI.
    invokeLater(new Runnable() {
      public void run() {
        createAndShowGUI();
      }
    });
  }
}

@FunctionalInterface
interface MouseClicker extends MouseListener {
  @Override
  default void mousePressed(MouseEvent e) {

  }

  @Override
  default void mouseReleased(MouseEvent e) {

  }

  @Override
  default void mouseEntered(MouseEvent e) {

  }

  @Override
  default void mouseExited(MouseEvent e) {

  }
}