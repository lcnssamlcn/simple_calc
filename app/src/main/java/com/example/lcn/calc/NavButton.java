package com.example.lcn.calc;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

/**
 * navigation button in this calculator app, such as the left navigation button and right navigation
 * button to move the cursor left or right. An important feature is added so that users can
 * hold the button and periodically move the cursor. Constantly clicking the navigation button
 * to move the cursor per unit step is no longer needed.
 */
public class NavButton extends AppCompatButton {
    /**
     * listen the event that when the user tries to click and hold this navigation button
     */
    public static interface OnHoldListener {
        /**
         * executed when the user holds this navigation button
         */
        public abstract void onHold();
    }

    /**
     * {@link OnHoldListener#onHold()} listener
     */
    private NavButton.OnHoldListener onHoldListener;
    /**
     * When the user clicks and holds this navigation button, a new thread calling
     * {@link OnHoldListener#onHold()} will be generated.
     * It will base on this value to call the {@link OnHoldListener#onHold()} periodically.
     */
    public static final long HOLD_COOLDOWN_INTERVAL = 100;
    /**
     * true if the user is still holding this navigation button. Otherwise false.
     */
    private volatile boolean holding;
    /**
     * handler to update the equation display
     */
    private Handler handler;

    /**
     * create a new navigation button
     * @param context context that creates this button
     * @param attrs unused
     */
    public NavButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.onHoldListener = null;
        this.holding = false;
    }

    /**
     * set the {@link NavButton.OnHoldListener}
     * @param listener new listener
     */
    public void setOnHoldListener(NavButton.OnHoldListener listener) {
        this.onHoldListener = listener;
    }

    /**
     * when the user clicks and holds this navigation button, it will start a new thread to call
     * {@link OnHoldListener#onHold()} in a regular interval {@link #HOLD_COOLDOWN_INTERVAL}.
     * After the user releases the button, it will stop the thread.
     * @param event motion event that the user performed
     * @return true
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.i(MainActivity.TAG, "action: " + event.getAction());
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                if (this.onHoldListener != null) {
                    if (!this.holding) {
                        Log.i(MainActivity.TAG, "down/move");
                        this.holding = true;
                        if (this.handler == null)
                            this.handler = new Handler(getContext().getMainLooper());
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                while (NavButton.this.holding) {
                                    if (NavButton.this.onHoldListener != null) {
                                        NavButton.this.handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                NavButton.this.onHoldListener.onHold();
                                            }
                                        });
                                    }
                                    try {
                                        Thread.sleep(NavButton.HOLD_COOLDOWN_INTERVAL);
                                    }
                                    catch (InterruptedException e) {
                                        Log.e(MainActivity.TAG, Log.getStackTraceString(e));
                                        break;
                                    }
                                }
                            }
                        }).start();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                Log.i(MainActivity.TAG, "up");
                performClick();
                this.holding = false;
                break;
        }
        return true;
    }

    /**
     * support accessibility feature
     * @return true after the user releases this navigation button. If the user is still holding the
     *         button, return false.
     */
    @Override
    public boolean performClick() {
        return super.performClick();
    }
}
