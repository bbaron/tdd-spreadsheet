package app;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;


import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SheetTest {

  @Test
  void cellsAreEmptyByDefault() {
    Sheet sheet = new Sheet();
    assertEquals("", sheet.get("A1"));
    assertEquals("", sheet.get("ZX347"));
  }

  @Test
  void textCellsAreStored() {
    Sheet sheet = new Sheet();
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
    Sheet sheet = new Sheet();
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
    Sheet sheet = new Sheet();
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
    Sheet sheet = new Sheet();
    String theCell = "A21";

    sheet.put(theCell, text);
    assertEquals(text, sheet.getLiteral(theCell));
  }
}
