package zip;

import java.util.List;
import java.util.Stack;

/**
 * Created by IntelliJ IDEA.
 * User: jpcakal
 * Date: Jul 25, 2005
 * Time: 12:47:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class Add extends Operator {

    public Symbol evaluate(Stack operands) {
        Symbol operand1 = (Symbol)operands.pop();
        Symbol operand2 = (Symbol)operands.pop();
        return new Operand(operand2.valueOf().add(operand1.valueOf()));
    }

    public int precedence() {
        return 1;
    }
}
