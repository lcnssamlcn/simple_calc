package com.example.lcn.calc;

import android.util.Log;

import org.mariuszgromada.math.mxparser.Expression;

/**
 * This class serves for equation parsing and solving by using the 3rd-party library
 * <a href="http://mathparser.org">mXparser</a>.
 * @author lcn
 */
public class EqtSolver {
    /**
     * main application instance
     */
    private MainActivity mainActivity;
    /**
     * immutable equation. It is preserved in order to prevent accidental modification.
     */
    private final String eqt;
    /**
     * previous result shown in the {@link MainActivity#result result display}.
     */
    private String prevResult;
    /**
     * if an error occurred while solving the equation, it will be recorded in this variable. It will
     * be either one of the following:
     * <ul>
     *     <li>{@link #RESULT_NAN NaN}</li>
     *     <li>{@link #RESULT_SYN_ERR Syntax Error}</li>
     *     <li>{@link #RESULT_POS_INFTY positive infinity}</li>
     *     <li>{@link #RESULT_NEG_INFTY negative infinity}</li>
     * </ul>
     * If no error occurred, it will be <code>null</code>.
     */
    private String err;

    /**
     * shown in the {@link MainActivity#result result display} when the computation result is {@link Double#NaN}.
     */
    public static final String RESULT_NAN = "NaN";
    /**
     * shown in the {@link MainActivity#result result display} when a syntax error occurs, such as
     * missing closing parenthesis.
     */
    public static final String RESULT_SYN_ERR = "Syntax Error";
    /**
     * shown in the {@link MainActivity#result result display} when the computation result is {@link Double#POSITIVE_INFINITY}.
     */
    public static final String RESULT_POS_INFTY = "Infinity";
    /**
     * shown in the {@link MainActivity#result result display} when the computation result is {@link Double#NEGATIVE_INFINITY}.
     */
    public static final String RESULT_NEG_INFTY = "-Infinity";

    /**
     * create a new equation solver
     * @param mainActivity current application instance
     * @param eqt equation that currently appears in the {@link MainActivity#eqt equation display}
     * @param prevResult result that currently appears in the {@link MainActivity#result result display}
     */
    public EqtSolver(MainActivity mainActivity, String eqt, String prevResult) {
        this.mainActivity = mainActivity;
        this.eqt = eqt;
        this.prevResult = prevResult;
        this.err = null;
    }

    /**
     * remove the {@link MainActivity#CURSOR cursor} in the equation if exists
     * @param eqt equation
     * @return new string that has the cursor removed
     */
    public String removeCursor(String eqt) {
        StringBuilder sb = new StringBuilder(eqt);
        int cursorPos = sb.indexOf(MainActivity.CURSOR);
        if (cursorPos != -1)
            sb.deleteCharAt(cursorPos);
        return sb.toString();
    }

    /**
     * check if there is any syntax error in the equation
     * @param eqt equation equation to check
     * @return true if so; otherwsie false.
     */
    public boolean hasSyntaxError(final String eqt) {
        StringBuilder sb = new StringBuilder(eqt);
        sb.replace(0, sb.length(), removeCursor(sb.toString()));
        while (true) {
            int ansPos = sb.indexOf(Symbol.SYM_ANS.getRepr());
            if (ansPos == -1)
                break;
            sb.replace(ansPos, ansPos + Symbol.SYM_ANS.getRepr().length(), MainActivity.CURSOR);
            try {
                Symbol prevSym = this.mainActivity.getPrevSymbol(sb.toString());
                Symbol nextSym = this.mainActivity.getNextSymbol(sb.toString());
                if (prevSym != null && (Symbol.isNum(prevSym) || prevSym.equals(Symbol.SYM_RIGHT_PAREN) || prevSym.equals(Symbol.SYM_DOT) || prevSym.equals(Symbol.SYM_ANS)))
                    return true;
                if (nextSym != null && (Symbol.isNum(nextSym) || nextSym.equals(Symbol.SYM_LEFT_PAREN) || nextSym.equals(Symbol.SYM_DOT) || nextSym.equals(Symbol.SYM_ANS)))
                    return true;
                sb.replace(0, sb.length(), removeCursor(sb.toString()));
            }
            catch (RuntimeException e) {
                Log.e(MainActivity.TAG, e.getMessage());
                return true;
            }
        }
        return false;
    }

    /**
     * solve the equation
     * @return computation result in string form which is convenient to pass to the
     *         {@link MainActivity#result result display} without the need to cast afterwards.
     */
    public String solve() {
        StringBuilder sb = new StringBuilder(this.eqt);
        sb.replace(0, sb.length(), removeCursor(this.eqt));
        if (hasSyntaxError(sb.toString())) {
            this.err = EqtSolver.RESULT_SYN_ERR;
            return this.err;
        }
        sb.replace(0, sb.length(), sb.toString().replace(Symbol.SYM_ANS.getRepr(), this.prevResult));
        sb = new StringBuilder(Symbol.toExpr(sb.toString()));
        Log.i(MainActivity.TAG, "transformed eqt: " + sb.toString());
        Expression expr = new Expression(sb.toString());

        if (!expr.checkSyntax()) {
            this.err = EqtSolver.RESULT_SYN_ERR;
            return this.err;
        }
        double result = expr.calculate();
        if (result == Double.NaN) {
            this.err = EqtSolver.RESULT_NAN;
            return this.err;
        }
        else if (result == Double.POSITIVE_INFINITY) {
            this.err = EqtSolver.RESULT_POS_INFTY;
            return this.err;
        }
        else if (result == Double.NEGATIVE_INFINITY) {
            this.err = EqtSolver.RESULT_NEG_INFTY;
            return this.err;
        }
        return "" + result;
    }

    /**
     * check if any error has occurred.<br />
     * See {@link #err} for all possible errors that can occur during computation.
     * @return true if so; otherwise false.
     */
    public boolean hasErr() {
        return this.err != null;
    }

    /**
     * @return the error occurred during computation
     * @see #err
     */
    public String getErr() {
        return this.err;
    }

    /**
     * check if the user has encountered any syntax error or math error.<br />
     * See {@link #err} for all possible errors that can occur during computation.
     * @param prevResult result currently shown in the {@link MainActivity#result result display} before
     *                   computation.
     * @return true if so; otherwie false.
     */
    public static boolean hadErr(String prevResult) {
        return prevResult.equals(EqtSolver.RESULT_SYN_ERR) || prevResult.equals(EqtSolver.RESULT_NAN) || prevResult.equals(EqtSolver.RESULT_POS_INFTY) || prevResult.equals(EqtSolver.RESULT_NEG_INFTY);
    }
}
