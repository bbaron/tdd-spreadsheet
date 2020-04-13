package app.swing;

/*
 * TableSelectionDemo.java requires no other files.
 */

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static javax.swing.JFrame.*;
import static javax.swing.SwingUtilities.invokeLater;

public class TableSelectionDemo extends JPanel
    implements ActionListener {
  private JTable table;
  private JCheckBox rowCheck;
  private JCheckBox columnCheck;
  private JCheckBox cellCheck;
  private ButtonGroup buttonGroup;
  private JTextArea output;

  public TableSelectionDemo() {
    super();
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

    table = new JTable(new MyTableModel());
    table.setPreferredScrollableViewportSize(new Dimension(500, 70));
    table.setFillsViewportHeight(true);
    table.getSelectionModel().addListSelectionListener(new RowListener());
    table.getColumnModel().getSelectionModel().
        addListSelectionListener(new ColumnListener());
    add(new JScrollPane(table));

    add(new JLabel("Selection Mode"));
    buttonGroup = new ButtonGroup();
    addRadio("Multiple Interval Selection").setSelected(true);
    addRadio("Single Selection");
    addRadio("Single Interval Selection");

    add(new JLabel("Selection Options"));
    rowCheck = addCheckBox("Row Selection");
    rowCheck.setSelected(true);
    columnCheck = addCheckBox("Column Selection");
    cellCheck = addCheckBox("Cell Selection");
    cellCheck.setEnabled(false);

    output = new JTextArea(5, 40);
    output.setEditable(false);
    add(new JScrollPane(output));
  }

  private JCheckBox addCheckBox(String text) {
    JCheckBox checkBox = new JCheckBox(text);
    checkBox.addActionListener(this);
    add(checkBox);
    return checkBox;
  }

  private JRadioButton addRadio(String text) {
    JRadioButton b = new JRadioButton(text);
    b.addActionListener(this);
    buttonGroup.add(b);
    add(b);
    return b;
  }

  public void actionPerformed(ActionEvent event) {
    String command = event.getActionCommand();
    //Cell selection is disabled in Multiple Interval Selection
    //mode. The enabled state of cellCheck is a convenient flag
    //for this status.
    if ("Row Selection".equals(command)) {
      table.setRowSelectionAllowed(rowCheck.isSelected());
      //In MIS mode, column selection allowed must be the
      //opposite of row selection allowed.
      if (!cellCheck.isEnabled()) {
        table.setColumnSelectionAllowed(!rowCheck.isSelected());
      }
    } else if ("Column Selection".equals(command)) {
      table.setColumnSelectionAllowed(columnCheck.isSelected());
      //In MIS mode, row selection allowed must be the
      //opposite of column selection allowed.
      if (!cellCheck.isEnabled()) {
        table.setRowSelectionAllowed(!columnCheck.isSelected());
      }
    } else if ("Cell Selection".equals(command)) {
      table.setCellSelectionEnabled(cellCheck.isSelected());
    } else if ("Multiple Interval Selection".equals(command)) {
      table.setSelectionMode(
          ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
      //If cell selection is on, turn it off.
      if (cellCheck.isSelected()) {
        cellCheck.setSelected(false);
        table.setCellSelectionEnabled(false);
      }
      //And don't let it be turned back on.
      cellCheck.setEnabled(false);
    } else if ("Single Interval Selection".equals(command)) {
      table.setSelectionMode(
          ListSelectionModel.SINGLE_INTERVAL_SELECTION);
      //Cell selection is ok in this mode.
      cellCheck.setEnabled(true);
    } else if ("Single Selection".equals(command)) {
      table.setSelectionMode(
          ListSelectionModel.SINGLE_SELECTION);
      //Cell selection is ok in this mode.
      cellCheck.setEnabled(true);
    }

    //Update checkboxes to reflect selection mode side effects.
    rowCheck.setSelected(table.getRowSelectionAllowed());
    columnCheck.setSelected(table.getColumnSelectionAllowed());
    if (cellCheck.isEnabled()) {
      cellCheck.setSelected(table.getCellSelectionEnabled());
    }
  }

  private void outputSelection() {
    output.append(String.format("Lead: %d, %d. ",
        table.getSelectionModel().getLeadSelectionIndex(),
        table.getColumnModel().getSelectionModel().
            getLeadSelectionIndex()));
    output.append("Rows:");
    for (int c : table.getSelectedRows()) {
      output.append(String.format(" %d", c));
    }
    output.append(". Columns:");
    for (int c : table.getSelectedColumns()) {
      output.append(String.format(" %d", c));
    }
    output.append(".\n");
  }

  private class RowListener implements ListSelectionListener {
    public void valueChanged(ListSelectionEvent event) {
      if (event.getValueIsAdjusting()) {
        return;
      }
      output.append("ROW SELECTION EVENT. ");
      outputSelection();
    }
  }

  private class ColumnListener implements ListSelectionListener {
    public void valueChanged(ListSelectionEvent event) {
      if (event.getValueIsAdjusting()) {
        return;
      }
      output.append("COLUMN SELECTION EVENT. ");
      outputSelection();
    }
  }

  static class MyTableModel extends AbstractTableModel {
    private String[] columnNames = {"First Name",
        "Last Name",
        "Sport",
        "# of Years",
        "Vegetarian"};
    private Object[][] data = {
        {"Kathy", "Smith",
            "Snowboarding", 5, false},
        {"John", "Doe",
            "Rowing", 3, true},
        {"Sue", "Black",
            "Knitting", 2, false},
        {"Jane", "White",
            "Speed reading", 20, true},
        {"Joe", "Brown",
            "Pool", 10, false}
    };

    public int getColumnCount() {
      return columnNames.length;
    }

    public int getRowCount() {
      return data.length;
    }

    public String getColumnName(int col) {
      return columnNames[col];
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
      data[row][col] = value;
      fireTableCellUpdated(row, col);
    }

  }

  /**
   * Create the GUI and show it.  For thread safety,
   * this method should be invoked from the
   * event-dispatching thread.
   */
  private static void createAndShowGUI() {
    //Disable boldface controls.
    UIManager.put("swing.boldMetal", false);

    //Create and set up the window.
    JFrame frame = new JFrame("TableSelectionDemo");
    frame.setDefaultCloseOperation(EXIT_ON_CLOSE);

    //Create and set up the content pane.
    TableSelectionDemo newContentPane = new TableSelectionDemo();
    newContentPane.setOpaque(true); //content panes must be opaque
    frame.setContentPane(newContentPane);

    //Display the window.
    frame.pack();
    frame.setVisible(true);
  }

  public static void main(String[] args) {
    //Schedule a job for the event-dispatching thread:
    //creating and showing this application's GUI.
    invokeLater(TableSelectionDemo::createAndShowGUI);
  }
}
