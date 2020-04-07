package app.lox;

class Token {
  final TokenType type;                                           
  final String lexeme;                                            
  final Object literal;                                           
  final int column;

  Token(TokenType type, String lexeme, Object literal, int column) {
    this.type = type;                                             
    this.lexeme = lexeme;                                         
    this.literal = literal;                                       
    this.column = column;
  }                                                               

  public String toString() {                                      
    return type + " " + lexeme + " " + literal;                   
  }                                                               
}       