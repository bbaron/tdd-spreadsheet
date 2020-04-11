package app;

import app.impl.SheetImpl;

import javax.swing.*;
import java.awt.*;

import static javax.swing.SwingUtilities.invokeLater;

class SheetFrame extends JPanel {
  final SheetTableModel tableModel;
  final JTable jTable;
  final JButton jButton;
  final JLabel jLabel;
  final JTextField jTextField;

  SheetFrame(SheetTableModel tableModel) {
    super(new GridLayout(2, 0));
    jButton = new JButton("OK");
    jLabel = new JLabel("label");
    jTextField = new JTextField("               ");
    JPanel buttonPanel = new JPanel(new FlowLayout());
    buttonPanel.add(jLabel);
    buttonPanel.add(jTextField);
    buttonPanel.add(jButton);
    this.tableModel = tableModel;
    this.jTable = new JTable(tableModel);
    jTable.setPreferredScrollableViewportSize(new Dimension(1000, 800));
    jTable.setFillsViewportHeight(true);
    jTable.setCellSelectionEnabled(true);
    jTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
    tableModel.setValueAt("22222", 0, 1);

    //Create the scroll pane and add the table to it.
    JScrollPane scrollPane = new JScrollPane(jTable);
    //Add the scroll pane to this panel.
    add(buttonPanel);
    add(scrollPane);
  }

  /**
   * Create the GUI and show it.  For thread safety,
   * this method should be invoked from the
   * event-dispatching thread.
   */
  private static void createAndShowGUI() {
    Sheet sheet = new SheetImpl();
    SheetTableModel model = new SheetTableModel(sheet);


    //Create and set up the window.
    SheetFrame sheetFrame = new SheetFrame(model);
    JFrame frame = new JFrame("Sheet");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    sheetFrame.setOpaque(true);
    frame.setContentPane(sheetFrame);

    frame.pack();
    frame.setVisible(true);
  }

  public static void main(String[] args) {
    //Schedule a job for the event-dispatching thread:
    //creating and showing this application's GUI.
    invokeLater(SheetFrame::createAndShowGUI);

  }
}
