package com.example.lcn.calc;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

/**
 * simple calculator app
 * @author lcn
 * @version 1.0
 */
public class MainActivity extends AppCompatActivity {
    static final String TAG = "MainActivity";
    /**
     * cursor in the equation display
     */
    public static final String CURSOR = "\u258A";
    /**
     * maximum amount of characters (digits) that the result can show
     */
    public static final int MAX_RESULT_DISPLAY_WIDTH = 16;

    /**
     * error indicating the cursor in the equation display is missing.
     * It will be logged with this string if this error occurred.
     */
    public static final String ERR_CURSOR_MISSING = "Error: Cursor is missing";
    /**
     * error indicating the symbol is undefined due to unknown reason.
     * It will be logged with this string if this error occurred.
     */
    public static final String ERR_SYM_UNDEFINED = "Error: Symbol is undefined";

    /**
     * true if the user has previously computed a valid result (without error like
     * NaN, positive infinity and so on). Otherwise it will be false.
     */
    private boolean hasCalculated;

    /**
     * equation display
     */
    TextView eqt;
    /**
     * calculation result display
     */
    TextView result;

    /**
     * @return the current cursor pos in equation display
     */
    public int getCursorPos() {
        return this.eqt.getText().toString().indexOf(MainActivity.CURSOR);
    }

    /**
     * @return the previous result shown in the result display. If error occurred, it will return 0.
     */
    public String getPrevResult() {
        String prevResult = this.result.getText().toString();
        if (EqtSolver.hadErr(prevResult))
            return "0";
        return prevResult;
    }

    /**
     * initialize special math symbols
     */
    private void initBtnText() {
        Button btnExp = (Button) findViewById(R.id.btn_exp);
        btnExp.setText(Html.fromHtml(getResources().getString(R.string.btn_exp)));
    }

    /**
     * insert a new symbol to the equation display. If the cursor is missing, it will throw
     * <code>RuntimeException</code> and the insertion operation will be aborted.
     * @param symbol symbol to insert
     * @throws RuntimeException if the cursor is missing
     */
    public void insertSymbol(String symbol) {
        String eqtStr = MainActivity.this.eqt.getText().toString();
        int cursorPos = MainActivity.this.getCursorPos();
        if (cursorPos == -1) {
            resetDisplay();
            throw new RuntimeException(MainActivity.ERR_CURSOR_MISSING);
        }
        StringBuilder sb = new StringBuilder();
        if (cursorPos > 0)
            sb.append(eqtStr.substring(0, cursorPos));
        sb.append(symbol);
        sb.append(MainActivity.CURSOR);
        if (cursorPos + 1 < eqtStr.length())
            sb.append(eqtStr.substring(cursorPos + 1));
        MainActivity.this.eqt.setText(sb.toString());
    }

    /**
     * @return the previous symbol of the cursor in the equation display. It must <b>NEVER</b> return
     *         null.
     */
    public Symbol getPrevSymbol() {
        return getPrevSymbol(MainActivity.this.eqt.getText().toString());
    }

    /**
     * find the previous symbol of the cursor.
     * @param eqt equation to search for
     * @return the previous symbol of the cursor if exists. Otherwise return null.
     * @throws RuntimeException if the cursor is missing
     */
    public Symbol getPrevSymbol(String eqt) {
        List<Symbol> sortedSymbols = Symbol.sort();
        int cursorPos = eqt.indexOf(MainActivity.CURSOR);
        if (cursorPos == -1) {
            resetDisplay();
            throw new RuntimeException(MainActivity.ERR_CURSOR_MISSING);
        }
        for (Symbol sym : sortedSymbols) {
            String symRepr = sym.getRepr();
            if (cursorPos - symRepr.length() < 0)
                continue;
            if (eqt.substring(cursorPos - symRepr.length(), cursorPos).equals(symRepr))
                return sym;
        }
        return null;
    }

    /**
     * @return the next symbol of the cursor in the equation display. It must <b>NEVER</b> return
     *         null.
     */
    public Symbol getNextSymbol() {
        return getNextSymbol(MainActivity.this.eqt.getText().toString());
    }

    /**
     * find the next symbol of the cursor.
     * @param eqt equation to search for
     * @return the next symbol of the cursor if exists. Otherwise return null.
     * @throws RuntimeException if the cursor is missing
     */
    public Symbol getNextSymbol(String eqt) {
        List<Symbol> sortedSymbols = Symbol.sort();
        // Log.i(MainActivity.TAG, "sorted: " + sortedSymbols.toString());
        int cursorPos = eqt.indexOf(MainActivity.CURSOR);
        if (cursorPos == -1) {
            resetDisplay();
            throw new RuntimeException(MainActivity.ERR_CURSOR_MISSING);
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
        return null;
    }

    /**
     * executed when a digit symbol such as "1", "2", "3" and etc. is clicked.
     * @param v clicked button. (It is castable to <i>Button</i>.)
     */
    public void clickNum(View v) {
        Symbol sym = Symbol.findSymbolByID(v.getId());
        if (sym == null)
            return;
        try {
            if (this.hasCalculated)
                MainActivity.this.eqt.setText(MainActivity.CURSOR);
            insertSymbol(sym.getRepr());
        }
        catch (RuntimeException e) {
            Log.e(MainActivity.TAG, e.getMessage());
        }
        finally {
            if (this.hasCalculated)
                this.hasCalculated = false;
        }
    }

    /**
     * executed when an operator symbol such as "(", ")", "+" and etc. is clicked.
     * @param v clicked button. (It is castable to <i>Button</i>.)
     */
    public void clickOp(View v) {
        Symbol sym = Symbol.findSymbolByID(v.getId());
        if (sym == null)
            return;
        try {
            if (this.hasCalculated) {
                if (v.getId() != Symbol.SYM_ANS.getID())
                    MainActivity.this.eqt.setText(Symbol.SYM_ANS.getRepr() + MainActivity.CURSOR);
                else
                    MainActivity.this.eqt.setText(MainActivity.CURSOR);
            }
            insertSymbol(sym.getRepr());
        }
        catch (RuntimeException e) {
            Log.e(MainActivity.TAG, e.getMessage());
        }
        finally {
            if (this.hasCalculated)
                this.hasCalculated = false;
        }
    }

    /**
     * initialize buttons, such as "AC", left navigation button and right navigation button.
     */
    private void initBtn() {
        Button btnAC = (Button) findViewById(R.id.btn_AC);
        btnAC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetDisplay();
                if (MainActivity.this.hasCalculated)
                    MainActivity.this.hasCalculated = false;
            }
        });
        Button btnLeft = (Button) findViewById(R.id.btn_left);
        btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String eqtStr = MainActivity.this.eqt.getText().toString();
                if (eqtStr.charAt(0) == MainActivity.CURSOR.charAt(0))
                    return;

                int cursorPos = getCursorPos();
                if (cursorPos == -1) {
                    Log.e(MainActivity.TAG, MainActivity.ERR_CURSOR_MISSING);
                    resetDisplay();
                    return;
                }
                Symbol sym = null;
                try {
                    sym = getPrevSymbol();
                    if (sym == null) {
                        Log.e(MainActivity.TAG, MainActivity.ERR_SYM_UNDEFINED);
                        resetDisplay();
                        return;
                    }
                }
                catch (RuntimeException e) {
                    Log.e(MainActivity.TAG, e.getMessage());
                    resetDisplay();
                    return;
                }
                Log.i(MainActivity.TAG, String.format("prev symbol: \"%s\"", sym.getRepr()));
                StringBuilder sb = new StringBuilder();
                if (cursorPos - sym.getRepr().length() > 0)
                    sb.append(eqtStr.substring(0, cursorPos - sym.getRepr().length()));
                sb.append(MainActivity.CURSOR);
                sb.append(sym.getRepr());
                if (cursorPos + 1 < eqtStr.length())
                    sb.append(eqtStr.substring(cursorPos + 1));
                MainActivity.this.eqt.setText(sb.toString());
                if (MainActivity.this.hasCalculated)
                    MainActivity.this.hasCalculated = false;
            }
        });
        Button btnRight = (Button) findViewById(R.id.btn_right);
        btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String eqtStr = MainActivity.this.eqt.getText().toString();
                if (eqtStr.charAt(eqtStr.length() - 1) == MainActivity.CURSOR.charAt(0))
                    return;

                int cursorPos = getCursorPos();
                if (cursorPos == -1) {
                    Log.e(MainActivity.TAG, MainActivity.ERR_CURSOR_MISSING);
                    resetDisplay();
                    return;
                }
                Symbol sym = null;
                try {
                    sym = getNextSymbol();
                    if (sym == null) {
                        Log.e(MainActivity.TAG, MainActivity.ERR_SYM_UNDEFINED);
                        resetDisplay();
                        return;
                    }
                }
                catch (RuntimeException e) {
                    Log.e(MainActivity.TAG, e.getMessage());
                    resetDisplay();
                    return;
                }
                Log.i(MainActivity.TAG, String.format("next symbol: \"%s\"", sym.getRepr()));
                StringBuilder sb = new StringBuilder();
                if (cursorPos > 0)
                    sb.append(eqtStr.substring(0, cursorPos));
                sb.append(sym.getRepr());
                sb.append(MainActivity.CURSOR);
                if (cursorPos + sym.getRepr().length() + 1 < eqtStr.length())
                    sb.append(eqtStr.substring(cursorPos + sym.getRepr().length() + 1));
                MainActivity.this.eqt.setText(sb.toString());
                if (MainActivity.this.hasCalculated)
                    MainActivity.this.hasCalculated = false;
            }
        });
        Button btnDel = (Button) findViewById(R.id.btn_del);
        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int cursorPos = getCursorPos();
                if (cursorPos == -1) {
                    Log.e(MainActivity.TAG, MainActivity.ERR_CURSOR_MISSING);
                    resetDisplay();
                    return;
                }
                if (cursorPos == 0)
                    return;
                Symbol sym = null;
                try {
                    sym = getPrevSymbol();
                    if (sym == null) {
                        Log.e(MainActivity.TAG, MainActivity.ERR_SYM_UNDEFINED);
                        resetDisplay();
                        return;
                    }
                }
                catch (RuntimeException e) {
                    Log.e(MainActivity.TAG, e.getMessage());
                    resetDisplay();
                    return;
                }

                String eqtStr = MainActivity.this.eqt.getText().toString();
                StringBuilder sb = new StringBuilder();
                int end = cursorPos - sym.getRepr().length();
                if (end > 0)
                    sb.append(eqtStr.substring(0, end));
                sb.append(MainActivity.CURSOR);
                if (cursorPos + 1 < eqtStr.length())
                    sb.append(eqtStr.substring(cursorPos + 1));
                MainActivity.this.eqt.setText(sb.toString());
                if (MainActivity.this.hasCalculated)
                    MainActivity.this.hasCalculated = false;
            }
        });
        Button btnEqual = (Button) findViewById(R.id.btn_equal);
        btnEqual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EqtSolver eqtSolver = new EqtSolver(MainActivity.this, MainActivity.this.eqt.getText().toString(), MainActivity.this.getPrevResult());
                String result = eqtSolver.solve();
                if (!eqtSolver.hasErr()) {
                    if (result.length() > MainActivity.MAX_RESULT_DISPLAY_WIDTH) {
                        int expPos = result.indexOf("E");
                        if (expPos == -1)
                            MainActivity.this.result.setText(result.substring(0, MainActivity.MAX_RESULT_DISPLAY_WIDTH));
                        else {
                            int expW = result.length() - expPos;
                            MainActivity.this.result.setText(result.substring(0, MainActivity.MAX_RESULT_DISPLAY_WIDTH - expW) + result.substring(expPos));
                        }
                    }
                    else
                        MainActivity.this.result.setText(result);
                    MainActivity.this.hasCalculated = true;
                }
                else
                    MainActivity.this.result.setText(result);
            }
        });
    }

    /**
     * initialize the calculator display
     */
    private void initDisplay() {
        this.eqt = (TextView) findViewById(R.id.eqt);
        this.result = (TextView) findViewById(R.id.result);
        resetDisplay();
    }

    /**
     * reset the calculator display
     */
    private void resetDisplay() {
        this.eqt.setText(MainActivity.CURSOR);
        this.result.setText("0");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initBtnText();
        initDisplay();
        initBtn();
    }
}
