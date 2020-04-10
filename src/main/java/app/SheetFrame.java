package app;

import app.impl.SheetImpl;

import javax.swing.*;

import static javax.swing.SwingUtilities.invokeLater;

class SheetFrame extends JFrame {
  final SheetTableModel model;



  SheetFrame(SheetTableModel model) {
    super("Sheet");
    this.model = model;
  }
  /**
   * Create the GUI and show it.  For thread safety,
   * this method should be invoked from the
   * event-dispatching thread.
   */
  private static void createAndShowGUI() {
     Sheet sheet = new SheetImpl();
     SheetTableModel table = new SheetTableModel(sheet);
     SheetTableModel model = new SheetTableModel(sheet);

    //Create and set up the window.
    SheetFrame frame = new SheetFrame(model);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    //Add the ubiquitous "Hello World" label.
    JLabel label = new JLabel("Hello World");
    frame.getContentPane().add(label);

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
