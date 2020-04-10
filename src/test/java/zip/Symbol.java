package zip;

import java.math.BigDecimal;
import java.util.List;
import java.util.Stack;
import java.util.StringTokenizer;

/**
 * Created by IntelliJ IDEA.
 * User: jpcakal
 * Date: Jul 25, 2005
 * Time: 12:38:34 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class Symbol {
    private static final String EXPONENT = "^";
    private static final String MULTIPLY = "*";
    private static final String DIVIDE = "/";
    private static final String ADD = "+";
    private static final String SUBTRACT = "-";
    private static final String LPAREN = "(";
    private static final String RPAREN = ")";
    private static final String OPERATOR_TOKENS = ADD + SUBTRACT + MULTIPLY + DIVIDE + EXPONENT;
    private static final String SPECIAL_TOKENS = OPERATOR_TOKENS + LPAREN + RPAREN;

    public abstract void toPostfix(List postfix, Stack eval);

    public abstract Symbol evaluate(Stack operands);

    public abstract int precedence();

    public BigDecimal valueOf() {
        return null;
    }

    public static StringTokenizer getTokenizer(String expression, boolean returnDelims) {
        return new StringTokenizer(expression, SPECIAL_TOKENS, returnDelims);
    }

    public static Symbol createSymbol(String token, Sheet sheet) {
        if (isOperand(token))
            return new Operand(token);
        else if (isCellRef(token))
            return new CellRef(token, sheet);
        switch (token) {
            case EXPONENT:
                return new Exponent();
            case MULTIPLY:
                return new Multiply();
            case DIVIDE:
                return new Divide();
            case ADD:
                return new Add();
            case SUBTRACT:
                return new Subtract();
            case LPAREN:
                return new LeftParen();
            case RPAREN:
                return new RightParen();
            default:
                return null;
        }
    }

    private static boolean isOperand(String token) {
        return Character.isDigit(token.charAt(0));
    }

    private static boolean isCellRef(String token) {
        return Character.isLetter(token.charAt(0));
    }
}
