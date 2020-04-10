package edu.duke.cs.www2.courses.fall06.cps100.code.spread;

import java.awt.*;
import java.util.ArrayList;

/**
 * This class specifies the
 * cell format.
 *
 * @author Thierry Manfé
 * @version 1.0 July-2002
 */
class SheetCell {

  /**
   * Set this field to true and recompile
   * to get debug traces
   */
  public static final boolean DEBUG = true;


  static final int UNDEFINED = 0;
  static final int EDITED = 1;
  static final int UPDATED = 2;
  static final boolean USER_EDITION = true;
  static final boolean UPDATE_EVENT = false;

  Object value;
  String formula;
  int state;
  ArrayList<SheetCell> listeners;
  ArrayList<SheetCell> listenees;
  Color background;
  Color foreground;
  int row;
  int column;

  SheetCell(int r, int c) {
    row = r;
    column = c;
    value = null;
    formula = null;
    state = UNDEFINED;
    listeners = new ArrayList<>();
    listenees = new ArrayList<>();
    background = Color.white;
    foreground = Color.black;
  }


  SheetCell(int r, int c, String value, String formula) {
    this(r, c);
    this.value = value;
    this.formula = formula;
  }

  void userUpdate() {

    // The user has edited the cell. The dependencies
    // on other cells may have changed:
    // clear the links to the listeners. They will be
    // reset during interpretation
    for (var listenee : listenees) {
      listenee.listeners.remove(this);
    }
    listenees.clear();

    SpreadSheetModel.interpreter.interpret(this, USER_EDITION);
    updateListeners();
    state = UPDATED;
  }

  void updateListeners() {
    for (var listener : listeners) {
      SpreadSheetModel.interpreter.interpret(listener, UPDATE_EVENT);
      if (DEBUG) System.out.println("Listener updated: " + toStr());
      listener.updateListeners();
    }
  }

  private String toStr() {
    /*
      Object value;
  String formula;
  int state;
  ArrayList<SheetCell> listeners;
  ArrayList<SheetCell> listenees;
  Color background;
  Color foreground;
  int row;
  int column;
     */
    return String.format("SheetCell(row=%s, col=%s, formula=%s, value=%s, state=%s",
        row, column, formula, value, state);
  }

  public String toString() {
    if (state == EDITED && formula != null)
      return formula;
    else if (value != null)
      return value.toString();
    else
      return null;
  }

}