package zip;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.StringTokenizer;

/**
 * Created by IntelliJ IDEA.
 * User: jpcakal
 * Date: Jul 25, 2005
 * Time: 11:10:54 AM
 * To change this template use File | Settings | File Templates.
 */
@SuppressWarnings("ALL")
public class Formula {
    private Sheet sheet;

    public Formula(Sheet sheet) {
        this.sheet = sheet;
    }

    public String evaluate(String expression) {
        return evaluate(toPostfix(expression));
    }

    private List toPostfix(String expression) {
        List postfix = new ArrayList();
        Stack eval = new Stack();
        StringTokenizer tokenizer = Symbol.getTokenizer(expression, true);
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            Symbol symbol = Symbol.createSymbol(token, sheet);
            symbol.toPostfix(postfix, eval);
        }
        while (!eval.isEmpty()) {
            postfix.add(eval.pop());
        }
        return postfix;
    }

    private String evaluate(List postfix) {
        Stack operands = new Stack();
        while (!postfix.isEmpty()) {
            operands.push(((Symbol)postfix.remove(0)).evaluate(operands));
        }
        return operands.pop().toString();
    }
}
