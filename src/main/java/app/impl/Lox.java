package app.impl;

import app.exceptions.RuntimeError;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

@SuppressWarnings("unused")
public class Lox {
  static boolean hadError = false;
  static boolean hadRuntimeError = false;
  private static final Environment environment = new Environment();
  private static final References references = new References();
  private static final Interpreter interpreter = new Interpreter(environment, references);

  public static void main(String[] args) throws IOException {
    if (args.length > 1) {
      System.out.println("Usage: jlox [script]");
      System.exit(64);
    } else if (args.length == 1) {
      runFile(args[0]);
    } else {
      runPrompt();
    }
  }

  private static void runFile(String path) throws IOException {
    byte[] bytes = Files.readAllBytes(Paths.get(path));
    run(new String(bytes, Charset.defaultCharset()));
    // Indicate an error in the exit code.
    if (hadError) System.exit(65);
    if (hadRuntimeError) System.exit(70);
  }

  private static void runPrompt() throws IOException {
    InputStreamReader input = new InputStreamReader(System.in);
    BufferedReader reader = new BufferedReader(input);

    for (int i = 0; i < Integer.MAX_VALUE; i++) {
      hadError = false;
      System.out.print("> ");
      run(reader.readLine());
    }
  }

  private static void run(String source) {
//    Scanner scanner = new Scanner(source);
//    List<Token> tokens = scanner.scanTokens();
//    Parser parser = new Parser(tokens);
//    Expr expression = parser.parse();
//
//    // Stop if there was a syntax error.
//    if (hadError) return;

    interpreter.interpret(Key.of("A1"), source);
//    String expr = AstPrinter.printExpr(expression);
    System.out.printf("%s%n", environment.getOrDefault(Key.of("A1"), ""));
  }

  static void unexpectedChar(int column) {
    report(column, "", "Unexpected character.");
  }

  private static String report(int column, String where, String message) {
    String report =
        "[column " + column + "] Error" + where + ": " + message;
    hadError = true;
    return report;
  }

  private static String error(Token token, String message) {
    if (token.type() == TokenType.EOF) {
      return report(token.column(), " at end", message);
    } else {
      return report(token.column(), " at '" + token.lexeme() + "'", message);
    }
  }

  static String runtimeError(RuntimeError error) {
    String msg = error.getMessage();
    System.out.println(msg);
    hadRuntimeError = true;
    return msg;
  }
}
