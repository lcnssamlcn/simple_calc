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
    SYM_MUL("\u00D7", R.id.btn_mul),
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
     * create a new math symbol
     * @param repr representation in the {@link MainActivity#eqt equation display}.
     * @param id symbol button id
     */
    Symbol(String repr, int id) {
        this.repr = repr;
        this.id = id;
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
}
