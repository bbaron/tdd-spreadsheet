package app;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

public class TestTableModelListener implements TableModelListener {
  public boolean wasNotified = false;

  public void tableChanged(TableModelEvent e) {
    wasNotified = true;
  }
}