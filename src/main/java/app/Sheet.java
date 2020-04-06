package app;

public interface Sheet {

  String get(String key);

  void put(String key, String literal);

  String getLiteral(String key);

}

