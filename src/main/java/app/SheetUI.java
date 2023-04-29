package app;

import app.impl.SheetImpl;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.event.ActionEvent;

import static app.misc.Helpers.*;
import static java.awt.EventQueue.*;

/**
 * @author bbaron
 */
public class SheetUI extends javax.swing.JFrame {
  public static final int COL_COUNT = 25;
  public static final int ROW_COUNT = 50;
  final JTable table = new JTable();
  final JLabel label = new JLabel();
  final JButton button = new JButton();
  final JTextField field = new JTextField();
//  final SheetTableModel model;
  final TableModelCellChangeNotifier notifier;
  int selectedRow = Integer.MIN_VALUE;
  int selectedCol = Integer.MIN_VALUE;
  final SheetImpl sheet;

  private void layoutStuff() {
    JPanel jPanel = new JPanel();
    JScrollPane jScrollPane = new JScrollPane();
    GroupLayout jPanel3Layout = new GroupLayout(jPanel);
    jPanel.setLayout(jPanel3Layout);
    jPanel3Layout.setHorizontalGroup(
        jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane, GroupLayout.DEFAULT_SIZE, 505, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(label, GroupLayout.PREFERRED_SIZE, 128, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(field)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button))))
    );
    jPanel3Layout.setVerticalGroup(
        jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                    .addComponent(label)
                    .addComponent(field, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(button))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane, GroupLayout.DEFAULT_SIZE, 576, Short.MAX_VALUE)
                .addContainerGap())
    );

    GroupLayout layout = new GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
        layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
    );
    layout.setVerticalGroup(
        layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(jPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
    );
    jScrollPane.setViewportView(table);
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    pack();
  }

  public SheetUI() {
    notifier = new TableModelCellChangeNotifier();
    sheet = new SheetImpl(notifier);
//    model = new SheetTableModel(sheet);
    notifier.sheetTableModel = sheet;
    for (int row = 0; row < 10; row++) {
      for (int col = 1; col < 10; col++) {
        sheet.setValueAt("" + ((row + 1) * col + row + col), row, col);
      }
    }
    sheet.setValueAt("=(A1 + A2) * (B1 + B2)", 20, 1);
    label.setToolTipText("");
    field.setText("");
    button.setText("OK");
    button.addActionListener(this::okButtonActionPerformed);

    table.setModel(sheet);
    table.setColumnSelectionAllowed(true);
    table.setRowSelectionAllowed(true);
    table.setCellSelectionEnabled(true);
    getRowModel().setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
    getRowModel().addListSelectionListener(this::outputSelection);
    getColumnModel().addListSelectionListener(this::outputSelection);

    layoutStuff();
  }

  private void outputSelection(ListSelectionEvent e) {
    if (e.getValueIsAdjusting()) return;
    int leadRow = getRowModel().getLeadSelectionIndex();
    int leadCol = getColumnModel().getLeadSelectionIndex();
    if (leadCol < 1 || leadRow < 0) return;
    if (leadRow != selectedRow || leadCol != selectedCol) {
      System.out.printf("Lead: %d, %d key: %s", leadRow, leadCol, makeKey(leadRow, leadCol));
      field.setText(sheet.getLiteralValueAt(leadRow, leadCol));
      selectedRow = leadRow;
      selectedCol = leadCol;
    }
//    System.out.print("Rows:");
//    for (int c : table.getSelectedRows()) {
//      System.out.print(String.format(" %d", c));
//    }
//    System.out.print(" Columns:");
//    for (int c : table.getSelectedColumns()) {
//      System.out.print(String.format(" %d", c));
//    }
    System.out.println();
  }

  private ListSelectionModel getColumnModel() {
    return table.getColumnModel().getSelectionModel();
  }

  private ListSelectionModel getRowModel() {
    return table.getSelectionModel();
  }

  private void okButtonActionPerformed(ActionEvent evt) {
    String text = field.getText();
    sheet.setValueAt(text, selectedRow, selectedCol);
    sheet.fireTableCellUpdated(selectedRow, selectedCol);
  }

  private String makeKey(int row, int col) {
    return String.format("%s%s", getSpreadsheetColumnName(col), row + 1);
  }

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
//    try {
//      for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
//        if ("Nimbus".equals(info.getName())) {
//          UIManager.setLookAndFeel(info.getClassName());
//          break;
//        }
//      }
//    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
//      System.out.println("Unable to select a look-n-feel type deal");
//      ex.printStackTrace(System.out);
//    }

    /* Create and display the form */
    invokeLater(() -> new SheetUI().setVisible(true));
  }

}
