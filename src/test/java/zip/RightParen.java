package zip;

import java.util.List;
import java.util.Stack;

/**
 * Created by IntelliJ IDEA.
 * User: jpcakal
 * Date: Jul 25, 2005
 * Time: 1:25:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class RightParen extends Symbol {
    public void toPostfix(List postfix, Stack eval) {
       Symbol symbol = (Symbol)eval.pop();
       while (!(symbol instanceof LeftParen)) {
            postfix.add(symbol);
           symbol = (Symbol)eval.pop();
        }
    }

    public Symbol evaluate(Stack operands) {
        return null;
    }

    public int precedence() {
        return 0;
    }
}
