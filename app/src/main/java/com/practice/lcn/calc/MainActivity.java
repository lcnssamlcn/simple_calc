package com.practice.lcn.calc;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * simple calculator app
 * @author lcn
 * @version 1.0.1
 */
public class MainActivity extends AppCompatActivity {
    static final String TAG = "MainActivity";
    /**
     * cursor in the equation display
     */
    public static final String CURSOR = "\u258A";
    /**
     * maximum amount of characters that the {@link #result result display} can show
     */
    public static final int MAX_RESULT_DISPLAY_WIDTH = 16;
    /**
     * maximum amount of characters that the {@link #eqt equation display} can show
     */
    public static final int MAX_EQT_DISPLAY_WIDTH = 32;

    /**
     * duration to wait for the equation result to synchronize
     */
    public static final int RESULT_SYNC_DURATION = 25;

    /**
     * true if the user has previously computed a valid result (without error like
     * NaN, positive infinity and so on). Otherwise it will be false.
     * <p>
     * It is used for creating shortcuts such as
     * <ul>
     *     <li>when the user clicks an operator (plus symbol), the calculator will automatically
     *     prepend "Ans" to let the user use the previous result.</li>
     *     <li>when the user clicks a digit, the calculator will automatically erase the whole
     *     equation and append the digit.</li>
     * </ul>
     * </p>
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
     * responsible for deciphering which operation the user issued
     * (inserting a math symbol / deleting the previous
     * symbol / moving the cursor to the left / moving the cursor to the right) and rendering the
     * "relative" equation to deal with the equation display overflow problem.
     * @see EqtTextWatcher
     */
    private EqtTextWatcher eqtTextWatcher;

    /**
     * @return the previous result shown in the result display. If error occurred, it will return 0.
     */
    public String getPrevResult() {
        String prevResult = this.result.getText().toString();
        Log.i(MainActivity.TAG, "prevResult: " + prevResult);
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
     * @throws com.practice.lcn.calc.exception.CursorMissingException if the cursor is missing
     * @throws com.practice.lcn.calc.exception.SymbolUndefinedException if the symbol before the cursor is not defined in {@link Symbol}
     * @see EqtBuilder#insertSymbol(String, String)
     */
    public void insertSymbol(String symbol) {
        MainActivity.this.eqt.setText(EqtTextWatcher.OP_INS + " " + symbol);
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
            if (this.hasCalculated) {
                MainActivity.this.eqt.setText(String.format("%s %s", EqtTextWatcher.OP_SET, sym.getRepr() + MainActivity.CURSOR));
            }
            else {
                insertSymbol(sym.getRepr());
            }
        }
        catch (RuntimeException e) {
            Log.e(MainActivity.TAG, Log.getStackTraceString(e));
            resetDisplay();
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
                if (v.getId() != Symbol.SYM_ANS.getID()) {
                    MainActivity.this.eqt.setText(String.format("%s %s", EqtTextWatcher.OP_SET, Symbol.SYM_ANS.getRepr() + sym.getRepr() + MainActivity.CURSOR));
                }
                else
                    MainActivity.this.eqt.setText(String.format("%s %s", EqtTextWatcher.OP_SET, sym.getRepr() + MainActivity.CURSOR));
            }
            else
                insertSymbol(sym.getRepr());
        }
        catch (RuntimeException e) {
            Log.e(MainActivity.TAG, Log.getStackTraceString(e));
            resetDisplay();
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
                FlashEffect flashEffect = FlashEffect.getInstance(MainActivity.this);
                flashEffect.run(new FlashEffect.IFlashEffect() {
                    @Override
                    public void postCallback(Handler handler) {
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                resetDisplay();
                                flashEffect.setCompleted(true);
                            }
                        }, MainActivity.RESULT_SYNC_DURATION);
                        if (MainActivity.this.hasCalculated)
                            MainActivity.this.hasCalculated = false;
                    }
                });
            }
        });
        NavButton btnLeft = (NavButton) findViewById(R.id.btn_left);
        btnLeft.setOnHoldListener(new NavButton.OnHoldListener() {
            @Override
            public void onHold() {
                try {
                    MainActivity.this.eqt.setText(EqtTextWatcher.OP_LEFT);
                }
                catch (RuntimeException e) {
                    Log.e(MainActivity.TAG, Log.getStackTraceString(e));
                    resetDisplay();
                }
                finally {
                    if (MainActivity.this.hasCalculated)
                        MainActivity.this.hasCalculated = false;
                }
            }
        });
        NavButton btnRight = (NavButton) findViewById(R.id.btn_right);
        btnRight.setOnHoldListener(new NavButton.OnHoldListener() {
            @Override
            public void onHold() {
                try {
                    MainActivity.this.eqt.setText(EqtTextWatcher.OP_RIGHT);
                }
                catch (RuntimeException e) {
                    Log.e(MainActivity.TAG, Log.getStackTraceString(e));
                    resetDisplay();
                }
                finally {
                    if (MainActivity.this.hasCalculated)
                        MainActivity.this.hasCalculated = false;
                }
            }
        });
        Button btnDel = (Button) findViewById(R.id.btn_del);
        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    MainActivity.this.eqt.setText(EqtTextWatcher.OP_DEL);
                }
                catch (RuntimeException e) {
                    Log.e(MainActivity.TAG, Log.getStackTraceString(e));
                    resetDisplay();
                }
                finally {
                    if (MainActivity.this.hasCalculated)
                        MainActivity.this.hasCalculated = false;
                }
            }
        });
        Button btnEqual = (Button) findViewById(R.id.btn_equal);
        btnEqual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FlashEffect flashEffect = FlashEffect.getInstance(MainActivity.this);
                Log.i(MainActivity.TAG, "completed: " + flashEffect.isCompleted());
                if (!flashEffect.isCompleted())
                    return;

                EqtSolver eqtSolver = new EqtSolver(MainActivity.this, MainActivity.this.eqtTextWatcher.getEqt(), MainActivity.this.getPrevResult());
                String result = eqtSolver.solve();
                flashEffect.run(new FlashEffect.IFlashEffect() {
                    @Override
                    public void postCallback(Handler handler) {
                        if (!eqtSolver.hasErr()) {
                            if (result.length() > MainActivity.MAX_RESULT_DISPLAY_WIDTH) {
                                int expPos = result.indexOf("E");
                                if (expPos == -1) {
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            MainActivity.this.result.setText(result.substring(0, MainActivity.MAX_RESULT_DISPLAY_WIDTH));
                                            flashEffect.setCompleted(true);
                                        }
                                    }, MainActivity.RESULT_SYNC_DURATION);
                                }
                                else {
                                    int expW = result.length() - expPos;
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            MainActivity.this.result.setText(result.substring(0, MainActivity.MAX_RESULT_DISPLAY_WIDTH - expW) + result.substring(expPos));
                                            flashEffect.setCompleted(true);
                                        }
                                    }, MainActivity.RESULT_SYNC_DURATION);
                                }
                            }
                            else {
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        MainActivity.this.result.setText(result);
                                        flashEffect.setCompleted(true);
                                    }
                                }, MainActivity.RESULT_SYNC_DURATION);
                            }
                            MainActivity.this.hasCalculated = true;
                        }
                        else {
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    MainActivity.this.result.setText(result);
                                    flashEffect.setCompleted(true);
                                }
                            }, MainActivity.RESULT_SYNC_DURATION);
                        }
                    }
                });
            }
        });
    }

    /**
     * initialize the calculator display
     */
    private void initDisplay() {
        this.eqt = (TextView) findViewById(R.id.eqt);
        this.result = (TextView) findViewById(R.id.result);
        this.eqtTextWatcher = new EqtTextWatcher(this);
        this.eqt.addTextChangedListener(this.eqtTextWatcher);
        resetDisplay();
    }

    /**
     * reset the calculator display
     */
    public void resetDisplay() {
        this.eqtTextWatcher.reset();
        this.eqt.setText(String.format("%s %s", EqtTextWatcher.OP_SET, MainActivity.CURSOR));
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
