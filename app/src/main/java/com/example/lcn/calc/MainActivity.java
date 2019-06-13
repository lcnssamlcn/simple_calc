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
 */
public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";
    /**
     * cursor in the equation display
     */
    private static final String CURSOR = "\u258A";

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
     * initialize special math symbols
     */
    private void initBtnText() {
        Button btnExp = (Button) findViewById(R.id.btn_exp);
        btnExp.setText(Html.fromHtml(getResources().getString(R.string.btn_exp)));
    }

    /**
     * insert a new symbol to the equation display.
     * @param symbol symbol to insert
     */
    public void insertSymbol(String symbol) {
        String eqtStr = MainActivity.this.eqt.getText().toString();
        int cursorPos = MainActivity.this.getCursorPos();
        if (cursorPos == -1) {
            resetDisplay();
            return;
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
     * @return the previous symbol in the equation display.
     */
    public Symbol getPrevSymbol() {
        List<Symbol> sortedSymbols = Symbol.sort();
        String eqtStr = MainActivity.this.eqt.getText().toString();
        int cursorPos = MainActivity.this.getCursorPos();
        if (cursorPos == -1) {
            resetDisplay();
            return null;
        }
        for (Symbol sym : sortedSymbols) {
            String symRepr = sym.getRepr();
            if (cursorPos - symRepr.length() < 0)
                continue;
            if (eqtStr.substring(cursorPos - symRepr.length(), cursorPos).equals(symRepr))
                return sym;
        }
        return null;
    }

    /**
     * @return the next symbol in the equation display.
     */
    public Symbol getNextSymbol() {
        List<Symbol> sortedSymbols = Symbol.sort();
        // Log.i(MainActivity.TAG, "sorted: " + sortedSymbols.toString());
        String eqtStr = MainActivity.this.eqt.getText().toString();
        int cursorPos = MainActivity.this.getCursorPos();
        if (cursorPos == -1) {
            resetDisplay();
            return null;
        }
        for (Symbol sym : sortedSymbols) {
            String symRepr = sym.getRepr();
            int endSym = cursorPos + symRepr.length() + 1;
            if (endSym == eqtStr.length()) {
                if (eqtStr.substring(cursorPos + 1).equals(symRepr))
                    return sym;
            }
            else if (endSym < eqtStr.length()) {
                if (eqtStr.substring(cursorPos + 1, endSym).equals(symRepr))
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
        insertSymbol(sym.getRepr());
    }

    /**
     * executed when an operator symbol such as "(", ")", "+" and etc. is clicked.
     * @param v clicked button. (It is castable to <i>Button</i>.)
     */
    public void clickOp(View v) {
        Symbol sym = Symbol.findSymbolByID(v.getId());
        if (sym == null)
            return;
        insertSymbol(sym.getRepr());
    }

    /**
     * initialize general buttons, such as "AC", left navigation button and right navigation button.
     */
    private void initBtnGeneral() {
        Button btnAC = (Button) findViewById(R.id.btn_AC);
        btnAC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetDisplay();
            }
        });
        /* nav */
        Button btnLeft = (Button) findViewById(R.id.btn_left);
        btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String eqtStr = MainActivity.this.eqt.getText().toString();
                if (eqtStr.charAt(0) == MainActivity.CURSOR.charAt(0))
                    return;

                int cursorPos = getCursorPos();
                if (cursorPos == -1) {
                    resetDisplay();
                    return;
                }
                Symbol sym = getPrevSymbol();
                // internal error
                if (sym == null) {
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
                    resetDisplay();
                    return;
                }
                Symbol sym = getNextSymbol();
                // internal error
                if (sym == null) {
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
        initBtnGeneral();
    }
}
