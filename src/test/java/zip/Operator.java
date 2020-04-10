package zip;

import java.util.List;
import java.util.Stack;

/**
 * Created by IntelliJ IDEA.
 * User: jpcakal
 * Date: Jul 25, 2005
 * Time: 1:27:40 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class Operator extends Symbol {

    public void toPostfix(List postfix, Stack eval) {
        while (true) {
            if (eval.isEmpty()) {
                eval.push(this);
                break;
            } else if (eval.peek() instanceof LeftParen) {
                eval.push(this);
                break;
            } else if (precedence() > ((Symbol)eval.peek()).precedence()) {
                eval.push(this);
                break;
            } else {
                postfix.add(eval.pop());
            }
        }
    }

    public String toString() {
        return "#Error";
    }
}
