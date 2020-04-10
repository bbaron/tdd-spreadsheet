package zip;

import java.util.List;
import java.util.Stack;

/**
 * Created by IntelliJ IDEA.
 * User: jpcakal
 * Date: Jul 25, 2005
 * Time: 1:23:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class LeftParen extends Symbol {
    public void toPostfix(List postfix, Stack eval) {
        eval.push(this);
    }

    public Symbol evaluate(Stack operands) {
        return null;
    }

    public int precedence() {
        return 0;
    }
}
