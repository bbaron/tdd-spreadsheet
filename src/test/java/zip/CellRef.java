package zip;

import java.util.List;
import java.util.Stack;

/**
 * Created by IntelliJ IDEA.
 * User: jpcakal
 * Date: Jul 25, 2005
 * Time: 4:44:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class CellRef extends Symbol {
    private String cell;
    private Sheet sheet;

    public CellRef(String cell, Sheet sheet) {
        this.cell = cell;
        this.sheet = sheet;
    }
    public void toPostfix(List postfix, Stack eval) {
        postfix.add(Symbol.createSymbol(sheet.getEvaluated(cell), sheet));
    }

    public Symbol evaluate(Stack operands) {
        return null;
    }

    public int precedence() {
        return 0;
    }
}
