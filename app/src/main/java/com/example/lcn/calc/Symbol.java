package com.example.lcn.calc;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * all symbols that are renderable in the equation display
 * @author lcn
 */
public enum Symbol {
    /**
     * digit 1
     */
    SYM_1("1", R.id.btn_1),
    /**
     * digit 2
     */
    SYM_2("2", R.id.btn_2),
    /**
     * digit 3
     */
    SYM_3("3", R.id.btn_3),
    /**
     * digit 4
     */
    SYM_4("4", R.id.btn_4),
    /**
     * digit 5
     */
    SYM_5("5", R.id.btn_5),
    /**
     * digit 6
     */
    SYM_6("6", R.id.btn_6),
    /**
     * digit 7
     */
    SYM_7("7", R.id.btn_7),
    /**
     * digit 8
     */
    SYM_8("8", R.id.btn_8),
    /**
     * digit 9
     */
    SYM_9("9", R.id.btn_9),
    /**
     * digit 0
     */
    SYM_0("0", R.id.btn_0),
    /**
     * left parenthesis "("
     */
    SYM_LEFT_PAREN("(", R.id.btn_left_paren),
    /**
     * right parenthesis ")"
     */
    SYM_RIGHT_PAREN(")", R.id.btn_right_paren),
    /**
     * multiplication operator
     */
    SYM_MUL("\u00D7", R.id.btn_mul, "*"),
    /**
     * division operator
     */
    SYM_DIV("/", R.id.btn_div),
    /**
     * subtraction operator
     */
    SYM_MINUS("-", R.id.btn_minus),
    /**
     * exponent operator
     */
    SYM_EXP("^(", R.id.btn_exp),
    /**
     * decimal operator
     */
    SYM_DOT(".", R.id.btn_dot),
    /**
     * plus operator
     */
    SYM_PLUS("+", R.id.btn_plus),
    /**
     * reference of the previous calculation result
     */
    SYM_ANS("Ans", R.id.btn_ans);

    /**
     * symbol representation in the {@link MainActivity#eqt equation display}.
     */
    private String repr;
    /**
     * symbol button id
     */
    private int id;
    /**
     * string representation that can be interpreted by the
     * {@link org.mariuszgromada.math.mxparser.Expression Expression}. It is only used when the
     * {@link org.mariuszgromada.math.mxparser.Expression Expression} cannot interpret {@link #repr},
     * the representation in the {@link MainActivity#eqt equation display}. If the {@link #repr}
     * can be interpreted, it will be left as <code>null</code>.
     */
    private String exprRepr;

    /**
     * create a new math symbol which can be interpreted by
     * {@link org.mariuszgromada.math.mxparser.Expression Expression}.
     * @param repr string representation in the {@link MainActivity#eqt equation display}.
     * @param id symbol button id in the keypad display
     */
    Symbol(String repr, int id) {
        this.repr = repr;
        this.id = id;
        this.exprRepr = null;
    }

    /**
     * create a new math symbol which cannot be directly interpreted by
     * {@link org.mariuszgromada.math.mxparser.Expression Expression} from its representation
     * {@link #repr} in the {@link MainActivity#eqt equation display}.
     * @param repr string representation in the equation display
     * @param id symbol button id in the keypad display
     * @param exprRepr string representation that can be interpreted by the
     *                 {@link org.mariuszgromada.math.mxparser.Expression Expression}.
     */
    Symbol(String repr, int id, String exprRepr) {
        this.repr = repr;
        this.id = id;
        this.exprRepr = exprRepr;
    }

    /**
     * @return symbol representation in the {@link MainActivity#eqt equation display}.
     */
    public String getRepr() {
        return this.repr;
    }

    /**
     * @return symbol button id
     */
    public int getID() {
        return this.id;
    }

    /**
     * @return the string representation that can be interpreted by the
     *         {@link org.mariuszgromada.math.mxparser.Expression Expression}.
     * @see #exprRepr
     */
    public String getExprRepr() {
        return this.exprRepr;
    }

    /**
     * check if the symbol is a digit
     * @param sym symbol to check
     * @return true if so; otherwise false.
     */
    public static boolean isNum(Symbol sym) {
        return sym.equals(Symbol.SYM_0) || sym.equals(Symbol.SYM_1) || sym.equals(Symbol.SYM_2) || sym.equals(Symbol.SYM_3) || sym.equals(Symbol.SYM_4) || sym.equals(Symbol.SYM_5) || sym.equals(Symbol.SYM_6) || sym.equals(Symbol.SYM_7) || sym.equals(Symbol.SYM_8) || sym.equals(Symbol.SYM_9);
    }

    /**
     * check if the symbol is an operator
     * @param sym symbol to check
     * @return true if so; otherwise false.
     */
    public static boolean isOp(Symbol sym) {
        return sym.equals(Symbol.SYM_LEFT_PAREN) || sym.equals(Symbol.SYM_RIGHT_PAREN) || sym.equals(Symbol.SYM_MUL) || sym.equals(Symbol.SYM_DIV) || sym.equals(Symbol.SYM_PLUS) || sym.equals(Symbol.SYM_MINUS) || sym.equals(Symbol.SYM_EXP) || sym.equals(Symbol.SYM_DOT);
    }

    /**
     * find symbol by button ID
     * @param id calculator button id
     * @return the corresponding symbol if found; otherwise null.
     */
    public static Symbol findSymbolByID(int id) {
        for (Symbol sym: Symbol.values()) {
            if (sym.getID() == id)
                return sym;
        }
        return null;
    }

    /**
     * sort all defined math symbols by the length of their string representation in descending order.
     * @return sorted symbols
     */
    public static List<Symbol> sort() {
        List<Symbol> sortedSymbols = Arrays.asList(Symbol.values());
        Collections.sort(sortedSymbols, new Comparator<Symbol>() {
            @Override
            public int compare(Symbol a, Symbol b) {
                return b.getRepr().length() - a.getRepr().length();
            }
        });
        return sortedSymbols;
    }

    /**
     * convert the equation in the equation display to a math expression which can be interpreted by
     * {@link org.mariuszgromada.math.mxparser.Expression Expression}.
     * @param eqt equation in the equation display
     * @return the math expression which can be interpreted by
     *         {@link org.mariuszgromada.math.mxparser.Expression Expression}.
     */
    public static String toExpr(String eqt) {
        StringBuilder sb = new StringBuilder(eqt);
        for (Symbol sym : Symbol.values()) {
            if (sym.getExprRepr() != null) {
                sb.replace(0, sb.length(), sb.toString().replace(sym.getRepr(), sym.getExprRepr()));
            }
        }
        return sb.toString();
    }
}
