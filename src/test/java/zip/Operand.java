package zip;

import java.util.List;
import java.util.Stack;
import java.math.BigDecimal;

/**
 * Created by IntelliJ IDEA.
 * User: jpcakal
 * Date: Jul 25, 2005
 * Time: 12:41:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class Operand extends Symbol {
    private BigDecimal value;

    public Operand(String token) {
        value = new BigDecimal(token);
    }

    public Operand(BigDecimal value) {
        this.value = value;
    }

    public void toPostfix(List postfix, Stack eval) {
        postfix.add(this);
    }

    public Symbol evaluate(Stack operands) {
        return this;
    }

    public int precedence() {
        return 0;
    }

    public BigDecimal valueOf() {
        return value;
    }

    public String toString() {
        return value.toString();
    }
}
