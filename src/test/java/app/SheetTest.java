package app;

import app.impl.SheetImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class SheetTest {
  private final Sheet sheet = new SheetImpl();

  @Test
  void cellsAreEmptyByDefault() {
    assertEquals("", sheet.get("A1"));
    assertEquals("", sheet.get("ZX347"));
  }

  @Test
  void textCellsAreStored() {
    String theKey = "A21";
    Stream<Executable> es = Stream.of("A string", "A different string", "")
        .map(s -> () -> {
          sheet.put(theKey, s);
          assertEquals(s, sheet.get(theKey));
        });
    assertAll(es);
  }

  @Test
  void manyCellsExist() {
    sheet.put("A1", "First");
    sheet.put("X27", "Second");
    sheet.put("ZX901", "Third");

    assertEquals("First", sheet.get("A1"));
    assertEquals("Second", sheet.get("X27"));
    assertEquals("Third", sheet.get("ZX901"));

    sheet.put("A1", "Fourth");
    assertEquals("Fourth", sheet.get("A1"));
    assertEquals("Second", sheet.get("X27"));
    assertEquals("Third", sheet.get("ZX901"));
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
}
