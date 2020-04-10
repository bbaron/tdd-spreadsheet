package app;

import app.exceptions.SheetError;
import app.impl.SheetImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.util.StopWatch;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class SheetTest {
  private final Sheet sheet = new SheetImpl();
//  private final Sheet sheet = new zip.Sheet();
  private final SheetTableModel table = new SheetTableModel(sheet);
  private final SheetTableModel model = new SheetTableModel(sheet);


  private static final int LAST_COLUMN_INDEX = 49;
  private static final int LAST_ROW_INDEX = 99;
  private final TableModelListener tableModelListener = mock(TableModelListener.class);

  @Test
  void cellsAreEmptyByDefault() {
    assertEquals("", sheet.get("A1"));
    assertEquals("", sheet.get("ZX347"));
  }

  @Test
  void cellKeysMustValidate() {
    assertThrows(SheetError.class, () -> sheet.get("=A1"));
  }

  @Test
  void undefinedCellRefsDefaultTo0() {
    sheet.put("A2", "=A1");
    assertEquals("0", sheet.get("A2"));
  }

  @Test
  void textCellsAreStored() {
    String theKey = "A21";
    Stream<Executable> es = Stream.of("A string", "A different string", "")
        .map(expected -> () -> {
          sheet.put(theKey, expected);
          var actual = sheet.get(theKey);
          assertEquals(expected, actual);
        });
    assertAll(es);
  }

  @Test
  void numbersAreStored() {
    sheet.put("A1", "13");
    assertEquals("13", sheet.get("A1"));
    sheet.put("A1", "13.1");
    assertEquals("13.1", sheet.get("A1"));
  }

  @Test
  void manyCellsExist() {
    sheet.put("A1", "First");
    sheet.put("X27", "Second");
    sheet.put("ZX901", "Third");

    assertAll("all",
        () -> assertAll("set 1",
            () -> assertEquals("First", sheet.get("A1")),
            () -> assertEquals("Second", sheet.get("X27")),
            () -> assertEquals("Third", sheet.get("ZX901"))),

        () -> {
          sheet.put("A1", "Fourth");
          assertAll("set 2",
              () -> assertEquals("Fourth", sheet.get("A1")),
              () -> assertEquals("Second", sheet.get("X27")),
              () -> assertEquals("Third", sheet.get("ZX901")));
        }
    );
  }

  @ParameterizedTest
  @ValueSource(strings = {"X99", "14", " 99 X", " 1234 :1234", " "})
  void numericCellsAreIdentifiedAndStored(String text) {
    String theKey = "A21";
    String[] a = text.split(":");
    String in = a[0];
    String out = a.length > 1 ? a[1] : in;

    sheet.put(theKey, in); // "Obvious" string
    assertEquals(out, sheet.get(theKey));
  }

  @ParameterizedTest
  @ValueSource(strings = {"some string", " 1432 ", "=7"})
  void haveAccessToCellLiteralValuesForEditing(String text) {
    String theCell = "A21";

    sheet.put(theCell, text);
    assertEquals(text, sheet.getLiteral(theCell));
  }

  @Test
  void formulaSpec() {
    sheet.put("B1", " =7"); // note leading space
    assertEquals(" =7", sheet.get("B1"), "Not a formula");
    assertEquals(" =7", sheet.getLiteral("B1"), "Unchanged");
  }

  @Test
  void constantFormula() {
    sheet.put("A1", "=7");
    assertAll(
        () -> assertEquals("=7", sheet.getLiteral("A1"), "Formula"),
        () -> assertEquals("7", sheet.get("A1"), "Value")
    );
  }

  @ParameterizedTest
  @ValueSource(strings = {"a1", "J23", "aa1", "Az1", "aA1"})
  void keysAreCaseSensitive(String key) {
    sheet.put(key, key);
    assertAll(
        () -> assertEquals(key, sheet.get(key.toUpperCase()), "upper"),
        () -> assertEquals(key, sheet.get(key), "original"),
        () -> assertEquals(key, sheet.get(key.toLowerCase()), "lower")
    );
  }

  @ParameterizedTest
  @MethodSource("formulaProvider")
  void simpleFormulae(String formula, String expected, String description) {
    sheet.put("A1", formula);
    assertEquals(expected, sheet.get("A1"), description);
  }

  static Stream<Arguments> formulaProvider() {
    return Stream.of(
        arguments("=(7)", "7", "Parenthetical"),
        arguments("=((((10))))", "10", "Deep parenthetical"),
        arguments("=2*3*4", "24", "Times"),
        arguments("=71+2+3", "76", "Plus"),
        arguments("=71-2-3", "66", "Minus"),
        arguments("=15/3", "5", "Divide"),
        arguments("=-3", "-3", "Unary minus"),
        arguments("=+3", "3", "Unary plus"),
        arguments("=3.1+0.2", "3.3", "floating point"),
        arguments("=3.1234", "3.1234", "floating point"),
        arguments("=3.12345", "3.1235", "floating point"),
        arguments("=3.00001", "3", "floating point"),
        arguments("=3.9999", "3.9999", "floating point"),
        arguments("=3.999", "3.999", "floating point"),
        arguments("=3.99", "3.99", "floating point"),
        arguments("=3.9", "3.9", "floating point"),
        arguments("=.9", "0.9", "floating point"),
        arguments("=0.9", "0.9", "floating point"),
        arguments("=7*(2+3)", "35", "Precedence 1"),
        arguments("=7*2+3", "17", "Precedence 2"),
        arguments("=7*(2+3)*((((2+1))))", "105", "Full expression")
    );
  }

  @Test
  void simpleFormulaError() {
    sheet.put("A1", "=7*");
    assertEquals("#Error", sheet.get("A1"));
  }

  @Test
  void simpleParenthesisError() {
    sheet.put("A1", "=(((((7))");
    assertEquals("#Error", sheet.get("A1"));
    sheet.put("A1", "=((7)))");
    assertEquals("#Error", sheet.get("A1"));
  }

  @Test
  void cellReferenceWorks() {
    sheet.put("A1", "8");
    sheet.put("A2", "=A1");
    assertEquals("8", sheet.get("A2"), "cell lookup");
  }

  @Test
  void formulasCanReferenceOtherCells() {
    sheet.put("A1", "8");
    sheet.put("A2", "3");
    sheet.put("B1", "=A1*(A1-A2)+A2/3");
    assertEquals("41", sheet.get("B1"), "calculation with cells");

  }

  @Test
  void formulasRecalculate() {
    sheet.put("A2", "3");
    sheet.put("B1", "=A2");
    sheet.put("A2", "6");
    assertEquals("6", sheet.get("B1"), "re-calculation");
  }

  @Test
  void deepPropagationWorks() {
    sheet.put("A1", "8");
    sheet.put("A2", "=A1");
    sheet.put("A3", "=A2");
    sheet.put("A4", "=A3");
    assertEquals("8", sheet.get("A4"), "deep propagation");
  }

  @Test
  void deepRecalculationWorks() {
    sheet.put("A1", "8");
    sheet.put("A2", "=A1");
    sheet.put("A3", "=A2");
    sheet.put("A4", "=A3");

    sheet.put("A2", "6");
    assertEquals("6", sheet.get("A4"), "deep re-calculation");

    sheet.put("B1", "0");
    assertEquals("0", sheet.get("B1"));
    sheet.put("B1", "=C1+C2+C3+C4");
    sheet.put("C2", "=C1");
    sheet.put("C1", "9");
    sheet.put("C3", "=C2");
    sheet.put("C4", "=C3");
    assertEquals("9", sheet.get("C1"));
    assertEquals("9", sheet.get("C2"));
    assertEquals("9", sheet.get("C3"));
    assertEquals("9", sheet.get("C4"));
    assertEquals("36", sheet.get("B1"));
  }

  @Test
  void formulaWorksWithManyCells() {
    sheet.put("A1", "10");
    sheet.put("A2", "=A1+B1");
    sheet.put("A3", "=A2+B2");
    sheet.put("A4", "=A3");
    sheet.put("B1", "7");
    sheet.put("B2", "=A2");
    sheet.put("B3", "=A3-A2");
    sheet.put("B4", "=A4+B3");

    assertEquals("34", sheet.get("A4"), "multiple expressions - A4");
    assertEquals("51", sheet.get("B4"), "multiple expressions - B4");
  }

  @Test
  void circularReferenceDoesntCrash() {
    sheet.put("A1", "=A1");
    assertTrue(true);
  }

  @Test
  void circularReferenceAdmitIt() {
    sheet.put("A1", "=A1");
    assertEquals("#Circular", sheet.get("A1"));
  }

  @Test
  void deepCircularity() {
    sheet.put("A1", "=A2");
    sheet.put("A2", "=A3");
    sheet.put("A3", "=A4");
    sheet.put("A4", "=A5");
    sheet.put("A5", "=A1");
    assertEquals("#Circular", sheet.get("A5"));
  }

  @Test
  void tableModelRequiredOverrides() {
    assertAll(
        () -> assertTrue(table.getColumnCount() > LAST_COLUMN_INDEX),
        () -> assertTrue(table.getRowCount() > LAST_ROW_INDEX),
        () -> assertEquals("", table.getValueAt(10, 10)));
  }

  @Test
  void columnNames() {
    assertAll(
        () -> assertEquals("", table.getColumnName(0)),
        () -> assertEquals("A", table.getColumnName(1)),
        () -> assertEquals("Z", table.getColumnName(26)),
        () -> assertEquals("AW", table.getColumnName(LAST_COLUMN_INDEX)));
  }

  @Test
  void column0ContainsIndex() {
    assertAll(
        () -> assertEquals("1", table.getValueAt(0, 0)),
        () -> assertEquals("50", table.getValueAt(49, 0)),
        () -> assertEquals("100", table.getValueAt(LAST_ROW_INDEX, 0)));
  }

  @Test
  void mainColumnsHaveContents() {
    sheet.put("A1", "upper left");
    sheet.put("A100", "lower left");
    sheet.put("AW1", "upper right");
    sheet.put("AW100", "lower right");

    assertAll(
        () -> assertEquals("upper left", table.getValueAt(0, 1)),
        () -> assertEquals("lower left", table.getValueAt(LAST_ROW_INDEX, 1)),
        () -> assertEquals("upper right", table.getValueAt(0, LAST_COLUMN_INDEX)),
        () -> assertEquals("lower right", table.getValueAt(LAST_ROW_INDEX, LAST_COLUMN_INDEX)));
  }

  @Test
  void storesWorkThroughTableModel() {
    assertAll(
        () -> assertAll(() -> {
          table.setValueAt("21", 0, 1);
          table.setValueAt("=A1", 1, 1);
          assertAll(
              () -> assertEquals("21", table.getValueAt(0, 1)),
              () -> assertEquals("21", table.getValueAt(1, 1)));
        }),
        () -> assertAll(() -> {
          table.setValueAt("22", 0, 1);
          assertAll(
              () -> assertEquals("22", table.getValueAt(0, 1)),
              () -> assertEquals("22", table.getValueAt(1, 1)));
        }));
  }

  @Test
  void column0isReadOnly() {
    assertThrowsIAE(() -> table.setValueAt("", 1, 0));
  }

  @Test
  void rowColBoundsChecked() {
    assertAll(
        assertThrowsIAE(() -> table.setValueAt("", -1, 1)),
        assertThrowsIAE(() -> table.setValueAt("", LAST_ROW_INDEX + 1, 1)),
        assertThrowsIAE(() -> table.setValueAt("", 0, -1)),
        assertThrowsIAE(() -> table.setValueAt("", 0, LAST_COLUMN_INDEX + 1)),
        assertThrowsIAE(() -> table.getValueAt(-1, 1)),
        assertThrowsIAE(() -> table.getValueAt(LAST_ROW_INDEX + 1, 1)),
        assertThrowsIAE(() -> table.getValueAt(0, -1)),
        assertThrowsIAE(() -> table.getValueAt(0, LAST_COLUMN_INDEX + 1))
    );
  }

  @Test
  void stressTest() {
    StopWatch watch = new StopWatch("stress test");
    sheet.put("A1", "1");
    for (int i = 1; i < 100; i++) {
      String key = "A" + (i + 1);
      String ref = "A" + i;
      String literal = String.format("=(%s+%s-%s+%s-%s+%s-%s+%s)*(%s/100)",
          ref, ref, ref, ref, ref, ref, ref, ref, ref);
      watch.start(key);
      sheet.put(key, literal);
      System.out.println(key + " = " + sheet.get(key));
      watch.stop();
//      System.out.println(watch.prettyPrint());
    }
    watch.start("update A1");
    sheet.put("A1", "2");
    watch.stop();
    System.out.println(watch.prettyPrint());
  }

  private Executable assertThrowsIAE(Executable e) {
    return () -> assertThrows(IllegalArgumentException.class, e);
  }

  /*
   * We've established that the table model can get and set values.
   * But JTable uses an event notification mechanism to find out
   * about the changes.
   *
   * To test this, we'll introduce a test helper class. It's a very
   * simple listener, and will assure us that notifications are
   * sent when changes are made.
   *
   * There's a couple of design decisions implicit here. One is that
   * we won't attempt to be specific about which cells change; we'll
   * just say that the table data has changed and let JTable refresh
   * its view of whichever cells it wants. (Because of cell dependencies,
   * changes in one cell could potentially no others, all others,
   * or anything in between.) We might revisit this decision during
   * performance tuning, and try to issue finer-grained notifications.
   *
   * The other decision is that we have no mechanism for our Sheet
   * to tell the table model about changes. So changes will either need
   * to come in through the table model, or we'll have to add some
   * notification mechanism to Sheet. For now, just make changes through the table model.
   */

  @Test
  void tableModelNotifies() {
    table.addTableModelListener(tableModelListener);
    table.setValueAt("22", 0, 1);
    verify(tableModelListener).tableChanged(any(TableModelEvent.class));
  }
  /*
   * Note the cast in our test here. Previous tests have been straight
   * implementations of TableModel functions; now we're saying that
   * our model has some extra functions. We'll face a small tradeoff later
   * when we want access to the feature: if we get the model back from JTable,
   * we'll have to cast it; if we don't want to cast it we'll have to
   * track it somewhere.
   *
   */

  @Test
  void sheetTableModelCanGetLiteral() {
    sheet.put("A1", "=7");
    String contents = table.getLiteralValueAt(0, 1);

    assertEquals("=7", contents);
  }
}
