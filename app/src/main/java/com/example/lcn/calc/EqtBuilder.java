package com.example.lcn.calc;

import com.example.lcn.calc.exception.CursorMissingException;
import com.example.lcn.calc.exception.SymbolUndefinedException;

import java.util.List;

/**
 * This namespace provides a set of useful functions for manipulating the equation.
 */
public class EqtBuilder {
    /**
     * get the cursor position from the equation given
     * @param eqt equation
     * @return a non-negative value if found; otherwise throw {@link CursorMissingException}.
     * @throws CursorMissingException if the cursor is missing in the equation given
     */
    public static int getCursorPos(String eqt) {
        int cursorPos = eqt.indexOf(MainActivity.CURSOR);
        if (cursorPos == -1) {
            throw new CursorMissingException();
        }
        return cursorPos;
    }

    /**
     * get the cusor position from the equation given. Comparing with
     * {@link EqtBuilder#getCursorPos(String)}, this method will not throw exception if the cursor is
     * missing in the equation but <code>-1</code> instead.
     * @param eqt equation
     * @return a non-negative value if found; otherwise return -1.
     */
    public static int getCursorPosNoException(String eqt) {
        return eqt.indexOf(MainActivity.CURSOR);
    }

    /**
     * insert a new symbol after the cusror to the equation given.
     * @param symbol symbol to insert
     * @return the new transformed equation
     * @throws CursorMissingException if the cursor is missing
     */
    public static String insertSymbol(String eqt, String symbol) {
        int cursorPos = EqtBuilder.getCursorPos(eqt);
        StringBuffer sb = new StringBuffer();
        if (cursorPos > 0)
            sb.append(eqt.substring(0, cursorPos));
        sb.append(symbol);
        sb.append(MainActivity.CURSOR);
        if (cursorPos + 1 < eqt.length())
            sb.append(eqt.substring(cursorPos + 1));
        return sb.toString();
    }

    /**
     * delete the symbol before the cursor in the equation given.
     * @param eqt equation
     * @return the new transformed equation
     * @throws CursorMissingException if the cursor is missing in the equation
     * @throws SymbolUndefinedException if the symbol before the cursor is not defined in {@link Symbol}
     */
    public static String deleteSymbol(String eqt) {
        int cursorPos = EqtBuilder.getCursorPos(eqt);
        if (cursorPos == 0) {
            return eqt;
        }
        Symbol symbol = EqtBuilder.getPrevSymbol(eqt);
        if (symbol == null) {
            return eqt;
        }
        StringBuffer sb = new StringBuffer();
        int end = cursorPos - symbol.getRepr().length();
        if (end > 0)
            sb.append(eqt.substring(0, end));
        sb.append(MainActivity.CURSOR);
        if (cursorPos + 1 < eqt.length())
            sb.append(eqt.substring(cursorPos + 1));
        return sb.toString();
    }

    /**
     * move the cursor to the left in the equation given.
     * @param eqt equation
     * @return the new transformed equation
     * @throws CursorMissingException if the cursor is missing in equation
     * @throws SymbolUndefinedException if the symbol before the cursor is not defined in {@link Symbol}
     */
    public static String moveCursorLeft(String eqt) {
        int cursorPos = EqtBuilder.getCursorPos(eqt);
        if (cursorPos == 0) {
            return eqt;
        }
        Symbol sym = EqtBuilder.getPrevSymbol(eqt);
        if (sym == null) {
            return eqt;
        }
        StringBuffer sb = new StringBuffer();
        if (cursorPos - sym.getRepr().length() > 0)
            sb.append(eqt.substring(0, cursorPos - sym.getRepr().length()));
        sb.append(MainActivity.CURSOR);
        sb.append(sym.getRepr());
        if (cursorPos + 1 < eqt.length())
            sb.append(eqt.substring(cursorPos + 1));
        return sb.toString();
    }

    /**
     * move the cursor to the right in the equation given.
     * @param eqt equation
     * @return the new transformed equation
     * @throws CursorMissingException if the cursor is missing in equation
     * @throws SymbolUndefinedException if the symbol after the cursor is not defined in {@link Symbol}
     */
    public static String moveCursorRight(String eqt) {
        int cursorPos = EqtBuilder.getCursorPos(eqt);
        if (cursorPos == eqt.length() - 1) {
            return eqt;
        }
        Symbol sym = EqtBuilder.getNextSymbol(eqt);
        if (sym == null) {
            return eqt;
        }
        StringBuffer sb = new StringBuffer();
        if (cursorPos > 0)
            sb.append(eqt.substring(0, cursorPos));
        sb.append(sym.getRepr());
        sb.append(MainActivity.CURSOR);
        if (cursorPos + sym.getRepr().length() + 1 < eqt.length())
            sb.append(eqt.substring(cursorPos + sym.getRepr().length() + 1));
        return sb.toString();
    }

    /**
     * find the previous symbol of the cursor.
     * @param eqt equation to search for
     * @return the previous symbol of the cursor if exists. If the cursor is at the front of the
     *         equation, it will return null.
     * @throws CursorMissingException if the cursor is missing
     * @throws SymbolUndefinedException if the symbol before the cursor is not defined in {@link Symbol}
     */
    public static Symbol getPrevSymbol(String eqt) {
        List<Symbol> sortedSymbols = Symbol.sort();
        int cursorPos = eqt.indexOf(MainActivity.CURSOR);
        if (cursorPos == 0) {
            return null;
        }
        for (Symbol sym : sortedSymbols) {
            String symRepr = sym.getRepr();
            if (cursorPos - symRepr.length() < 0)
                continue;
            if (eqt.substring(cursorPos - symRepr.length(), cursorPos).equals(symRepr))
                return sym;
        }
        throw new SymbolUndefinedException();
    }

    /**
     * find the next symbol of the cursor.
     * @param eqt equation to search for
     * @return the next symbol of the cursor if exists. If the cursor is at the end of the equation,
     *         it will return null.
     * @throws CursorMissingException if the cursor is missing
     * @throws SymbolUndefinedException if the symbol after the cursor is not defined in {@link Symbol}
     */
    public static Symbol getNextSymbol(String eqt) {
        List<Symbol> sortedSymbols = Symbol.sort();
        int cursorPos = eqt.indexOf(MainActivity.CURSOR);
        if (cursorPos == eqt.length() - 1) {
            return null;
        }
        for (Symbol sym : sortedSymbols) {
            String symRepr = sym.getRepr();
            int endSym = cursorPos + symRepr.length() + 1;
            if (endSym == eqt.length()) {
                if (eqt.substring(cursorPos + 1).equals(symRepr))
                    return sym;
            }
            else if (endSym < eqt.length()) {
                if (eqt.substring(cursorPos + 1, endSym).equals(symRepr))
                    return sym;
            }
        }
        throw new SymbolUndefinedException();
    }
}
