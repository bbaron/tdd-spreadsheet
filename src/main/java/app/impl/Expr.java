package app.impl;

sealed interface Expr {
  interface Visitor<R> {
    R visit(Binary expr);

    R visit(Grouping expr);

    R visit(Literal expr);

    R visit(Unary expr);

    R visit(Variable expr);
  }

  record Binary(Expr left, Token operator, Expr right) implements Expr {
    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.visit(this);
    }

  }

  record Grouping(Expr expression) implements Expr {
    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.visit(this);
    }
  }

  record Literal(Object value) implements Expr {

    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.visit(this);
    }

  }

  record Unary(Token operator, Expr right) implements Expr {

    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.visit(this);
    }
  }

  record Variable(Token name) implements Expr {

    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.visit(this);
    }

  }

  <R> R accept(Visitor<R> visitor);

  static Expr.Variable variable(Key key) {
    Token token = new Token(TokenType.IDENTIFIER, key.getValue(), null, 1, key);
    return new Expr.Variable(token);
  }
}
