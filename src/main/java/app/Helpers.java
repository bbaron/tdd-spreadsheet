package app;

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

}
