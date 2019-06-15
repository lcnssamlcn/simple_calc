package com.example.lcn.calc;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

/**
 * It keeps track of the user's action, transforms the equation accordingly and renders
 * the "relative" equation to deal with the equation text overflow problem. See {@link #newText} for
 * all possible actions that the user can perform.
 * @author lcn
 */
public class EqtTextWatcher implements TextWatcher {
    /**
     * main application instance
     */
    private MainActivity mainActivity;
    /**
     * equation before modification
     */
    private String oldText;
    /**
     * command from user
     * <ul>
     *     <li>{@link #OP_INS insert symbol after the cursor}</li>
     *     <li>{@link #OP_DEL delete symbol before the cursor}</li>
     *     <li>{@link #OP_LEFT move the cursor to the left}</li>
     *     <li>{@link #OP_RIGHT move the cursor to the right}</li>
     *     <li>{@link #OP_SET reset the entire equation}</li>
     * </ul>
     */
    private String newText;
    /**
     * full equation
     */
    private String eqt;
    /**
     * a 2-element tuple recording the view range in the {@link #eqt equation}. It is used for
     * dealing with the equation text overflow problem. THe first element records the starting index
     * of the {@link #eqt equation}; The second element records the ending index. <b>Note</b> that
     * (second element - first element + 1) must be equal to {@link MainActivity#MAX_EQT_DISPLAY_WIDTH}.
     */
    private int[] window;
    /**
     * recording whether error has occurred during equation display transformation. If so, it will
     * rethrow the exception to the caller and abort the transformation process.
     */
    private boolean errOccurred;
    /**
     * indicate whether it starts modifying the equation. It is used for dealing with the infinite
     * loop problem in TextWatcher while replacing a new text in {@link #afterTextChanged(Editable)}.
     */
    private boolean modified;

    /**
     * operation that the user tries to insert a symbol after the cursor
     * <dl>
     *     <dt>Synopsis</dt>
     *     <dd>insert &lt;symbol&gt;</dd>
     * </dl>
     */
    public static final String OP_INS = "insert";
    /**
     * operation that the user tries to delete a symbol before the cursor
     * <dl>
     *     <dt>Synopsis</dt>
     *     <dd>delete</dd>
     * </dl>
     */
    public static final String OP_DEL = "delete";
    /**
     * operation that the user tries to move the cursor to the left
     * <dl>
     *     <dt>Synopsis</dt>
     *     <dd>left</dd>
     * </dl>
     */
    public static final String OP_LEFT = "left";
    /**
     * operation that the user tries to move the cursor to the right
     * <dl>
     *     <dt>Synopsis</dt>
     *     <dd>right</dd>
     * </dl>
     */
    public static final String OP_RIGHT = "right";
    /**
     * operation that the user tries to erase the entire equation
     * <dl>
     *     <dt>Synopsis</dt>
     *     <dd>set &lt;new equation&gt;</dd>
     * </dl>
     */
    public static final String OP_SET = "set";

    /**
     * create a new equation text watcher
     * @param mainActivity main application instance
     */
    public EqtTextWatcher(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        this.reset();
    }

    /**
     * reset this watcher to default settings
     */
    public void reset() {
        this.oldText = null;
        this.newText = null;
        this.eqt = MainActivity.CURSOR;
        this.window = new int[] { 0, 0 };
        this.errOccurred = false;
        this.modified = false;
    }

    /**
     * check if the user has just tried to insert a new symbol after the cursor
     * @return true if so; otherwise false.
     * @see #OP_INS
     */
    public boolean isOpIns() {
        return this.newText.startsWith(EqtTextWatcher.OP_INS);
    }

    /**
     * check if the user has just tried to delete the symbol before the cursor
     * @return true if so; otherwise false.
     * @see #OP_DEL
     */
    public boolean isOpDel() {
        return this.newText.equals(EqtTextWatcher.OP_DEL);
    }

    /**
     * check if the user has just tried to move the cusor to the left.
     * @return true if so; otherwise false.
     * @see #OP_LEFT
     */
    public boolean isOpLeft() {
        return this.newText.equals(EqtTextWatcher.OP_LEFT);
    }

    /**
     * check if the user has just tried to move the cusor to the right.
     * @return true if so; otherwise false.
     * @see #OP_RIGHT
     */
    public boolean isOpRight() {
        return this.newText.equals(EqtTextWatcher.OP_RIGHT);
    }

    /**
     * check if the user has just tried to erase the entire equation.
     * @return true if so; otherwise false.
     * @see #OP_SET
     */
    public boolean isOpSet() {
        return this.newText.startsWith(EqtTextWatcher.OP_SET);
    }

    /**
     * get the full equation
     * @return the full equation text
     */
    public String getEqt() {
        return eqt;
    }

    /**
     * executed before the {@link MainActivity#eqt equation} is replaced with new text
     * @param oldText old equation text
     * @param start unused
     * @param beforeLen unused
     * @param afterLen unused
     */
    @Override
    public void beforeTextChanged(CharSequence oldText, int start, int beforeLen, int afterLen) {
        Log.i(MainActivity.TAG, String.format("before(\"%s\", %d, %d, %d)", oldText, start, beforeLen, afterLen));
        if (this.errOccurred)
            return;
        this.oldText = oldText.toString();
    }

    /**
     * executed when the {@link MainActivity#eqt equation} is replaced with new text. Note that the
     * first argument <code>newText</code> will be packed as the user's command.
     * See {@link #newText} for more details.
     * <p>
     * This function is responsible for interpreting which command the user tries to
     * perform and transforming the {@link #eqt equation} accordingly. At last, it will
     * update the {@link #window}&apos;s position.
     * </p>
     * @param newText new equation text
     * @param start unused
     * @param beforeLen unused
     * @param afterLen unused
     */
    @Override
    public void onTextChanged(CharSequence newText, int start, int beforeLen, int afterLen) {
        Log.i(MainActivity.TAG, String.format("on(\"%s\", %d, %d, %d)", newText, start, beforeLen, afterLen));
        if (this.errOccurred)
            return;
        this.newText = newText.toString();
        if (this.modified)
            return;

        try {
            if (isOpIns()) {
                this.eqt = EqtBuilder.insertSymbol(this.eqt, this.newText.split(" ")[1]);
            }
            else if (isOpDel()) {
                this.eqt = EqtBuilder.deleteSymbol(this.eqt);
            }
            else if (isOpLeft()) {
                this.eqt = EqtBuilder.moveCursorLeft(this.eqt);
            }
            else if (isOpRight()) {
                this.eqt = EqtBuilder.moveCursorRight(this.eqt);
            }
            else if (isOpSet()) {
                this.eqt = this.newText.split(" ")[1];
            }
        }
        catch (RuntimeException e) {
            this.errOccurred = true;
            throw e;
        }

        Log.i(MainActivity.TAG, String.format("on: eqt = \"%s\"", this.eqt));
        if (this.eqt.length() <= MainActivity.MAX_EQT_DISPLAY_WIDTH) {
            this.window[0] = 0;
            this.window[1] = this.eqt.length() - 1;
        }
        else {
            try {
                int cursorRelPos = 0;
                if (this.window[1] >= this.eqt.length() - 1) {
                    this.window[1] = this.eqt.length() - 1;
                    cursorRelPos = EqtBuilder.getCursorPosNoException(this.eqt.substring(this.window[0]));
                }
                else
                    cursorRelPos = EqtBuilder.getCursorPosNoException(this.eqt.substring(this.window[0], this.window[1] + 1));
                if (cursorRelPos == -1) {
                    int cursorAbsPos = EqtBuilder.getCursorPos(this.eqt);
                    if (cursorAbsPos - this.window[0] < 0) {
                        this.window[0] = cursorAbsPos;
                        this.window[1] = cursorAbsPos + MainActivity.MAX_EQT_DISPLAY_WIDTH - 1;
                        if (this.window[1] >= this.eqt.length()) {
                            this.window[1] = this.eqt.length() - 1;
                            this.window[0] = this.window[1] - MainActivity.MAX_EQT_DISPLAY_WIDTH + 1;
                        }
                    }
                    else if (cursorAbsPos - this.window[1] > 0) {
                        this.window[1] = cursorAbsPos;
                        this.window[0] = cursorAbsPos - MainActivity.MAX_EQT_DISPLAY_WIDTH + 1;
                        if (this.window[0] < 0) {
                            this.window[0] = 0;
                            this.window[1] = MainActivity.MAX_EQT_DISPLAY_WIDTH - 1;
                        }
                    }
                }
            }
            catch (RuntimeException e) {
                this.errOccurred = true;
                throw e;
            }
        }
    }

    /**
     * executed after the {@link #onTextChanged(CharSequence, int, int, int) equation transformation process} is done. This function is responsible for rendering the "relative" equation to the
     * {@link MainActivity#eqt equation display}.
     * @param newText used for replacing with "relative" equation
     */
    @Override
    public void afterTextChanged(Editable newText) {
        Log.i(MainActivity.TAG, String.format("after(\"%s\"); eqt = \"%s\"; win = [%d, %d]", newText.toString(), this.eqt, this.window[0], this.window[1]));
        if (this.modified) {
            this.modified = false;
            return;
        }
        if (this.errOccurred) {
            this.errOccurred = false;
            return;
        }

        this.modified = true;
        newText.replace(0, newText.length(), this.eqt, this.window[0], this.window[1] + 1);
    }
}
