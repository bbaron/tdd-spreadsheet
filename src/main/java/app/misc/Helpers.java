package app.misc;

public final class Helpers {
  private Helpers() {
    throw new InternalError();
  }

  public static String stringify(Object object) {
    if (object == null) return "";

    // Hack. Work around Java adding ".0" to integer-valued doubles.
    if (object instanceof Double) {
      String text = String.format("%.4f", object);
      int end = text.length() - 1;
      while (text.charAt(end) != '.') {
        if (text.charAt(end) != '0') break;
        end--;
      }
      text = text.substring(0, end + 1);
      if (text.charAt(text.length() - 1) == '.')
        text = text.substring(0, text.length() - 1);
      return text;
    }

    return object.toString();
  }

  public static String nullToEmpty(String s) {
    return s == null ? "" : s;
  }

  /**
   * Returns a default name for the column using spreadsheet conventions:
   * column 0 is empty, 1=A, 2=B, 3=C, ... 26=Z, 27=AA, 28=AB, etc.
   *
   * @param column the column [0, 100] being queried
   * @return a string containing the default name of <code>column</code>
   * @throws IllegalArgumentException If {@code column} cannot be found.
   */
  public static String getSpreadsheetColumnName(int column) {
    if (column < 0 || column > 100) throw new IllegalArgumentException(column + ": no such column");
    if (column == 0) return "";
    column--;
    StringBuilder result = new StringBuilder();
    for (; column >= 0; column = column / 26 - 1) {
      char colChar = (char) ((char) (column % 26) + 'A');
      result.insert(0, colChar);
    }
    return result.toString();
  }

  public static int getSpreadsheetColumnIndex(char column) {
    return Character.toUpperCase(column) - 'A' + 1;
  }

  public static int getSpreadsheetColumnIndex(String column) {
    if (column.isEmpty()) return 0;
    int result = 0;
    int len = column.length();
    var chars = reverse(column).toCharArray();
    for (int i = 0; i < len; i++) {
      int c = getSpreadsheetColumnIndex(chars[i]);
      result += c * Math.pow(26, i);
    }
    return result;
  }

  public static void main(String[] args) {
    String[] cols = {"", "A", "B", "C", "Y", "Z"
        , "AA", "AB", "AZ", "BA"
    };
//    char[] cols = "ABCDWXYZ".toCharArray();
    for (var col : cols) {
      int index = getSpreadsheetColumnIndex(col);
      System.out.printf("%s=%s,%s%n", col, getSpreadsheetColumnIndex(col), getSpreadsheetColumnName(index));
    }
//    System.out.println(getSpreadsheetColumnName(52));
//    System.out.println(getSpreadsheetColumnName(53));
//    System.out.println(getSpreadsheetColumnName(54));
//    System.out.println(getSpreadsheetColumnName(55));
  }

  private static String reverse(String start) {
    StringBuilder s = new StringBuilder();
    for (int i = 0; i < start.length(); i++) {
      s.insert(0, start.charAt(i));
    }
    return s.toString();

  }

}
