package zip;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: jpcakal
 * Date: Jul 22, 2005
 * Time: 12:22:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class Sheet implements app.Sheet {
    public static final String EMPTY_CELL_VALUE = "";
    public static final String FORMULA_CIRCULAR = "#Circular";
    public static final String FORMULA_ERROR = "#Error";

    private static final String REGEX_NUMBER = "\\s*\\d+\\s*";
    private static final String FORMULA_PREFIX = "=";
    private Map cells = new HashMap();
    private Map refs = new HashMap();

    public String getLiteral(String cell) {
        return fixEmptyCell((String) cells.get(cell));
    }

    public void put(String cell, String value) {
        cells.put(cell, value);
    }

    public String get(String cell) {
        clearCellMarks();
        try {
            return getEvaluated(cell);
        } catch (CircularReferenceException e) {
            return FORMULA_CIRCULAR;
        } catch (Exception e) {
            e.printStackTrace();
            return FORMULA_ERROR;
        }
    }

    String getEvaluated(String cell) {
        return trimIfNumber(applyFormula(cell, getLiteral(cell)));
    }

    private String applyFormula(String cell, String value) {
        return isFormula(value) ? compute(cell, value.substring(1)) : value;
    }

    private String fixEmptyCell(String value) {
        return value == null ? EMPTY_CELL_VALUE : value;
    }

    private String trimIfNumber(String value) {
        return isNumeric(value) ? value.trim() : value;
    }

    private boolean isNumeric(String value) {
        return value.matches(REGEX_NUMBER);
    }


    private boolean isFormula(String value) {
        return value.startsWith(FORMULA_PREFIX);
    }

    private String compute(String cell, String formula) {
        try {
            markCell(cell);
            return new Formula(this).evaluate(formula);
        } finally {
            clearCellMark(cell);
        }
    }

    private void clearCellMark(String cell) {
        refs.remove(cell);
    }

    private void clearCellMarks() {
        refs.clear();
    }

    private void markCell(String cell) {
        if (refs.containsKey(cell)) {
            throw new CircularReferenceException(cell);
        }
        refs.put(cell, cell);
    }

}
