package com.practice.lcn.calc;

import android.os.Handler;
import android.util.Log;

/**
 * This class helps create a flash effect on the {@link MainActivity#result result display} so that
 * the calculator will be more realistic.
 * @author lcn
 */
public class FlashEffect {
    /**
     * time duration for the flash effect (in ms)
     */
    public static final long FLASH_DURATION = 100;
    /**
     * lock of the flash effect to synchronize the {@link MainActivity#result equation result}
     */
    public static final Object LOCK = new Object();
    /**
     * singleton instance for generating a flash effect in the {@link MainActivity#result resukt display}
     */
    private static FlashEffect instance;

    /**
     * main application instance
     */
    private MainActivity mainActivity;
    /**
     * handler for creating the flash effect on the {@link MainActivity#result result display}.
     */
    private Handler handler;
    /**
     * true if the flash effect is completely rendered. Otherwise false.
     */
    private volatile boolean completed;

    /**
     * function pointer to allow provide a generic callback after the flash effect is over.
     */
    public static interface IFlashEffect {
        /**
         * executed after the flash effect is over. It is mainly used for setting the actual result
         * in the {@link MainActivity#result result display}. Note that this method will be called
         * in a new {@link Thread}.
         * @param handler given so that it will be useful to call the UI thread to
         *                set the actual result in the {@link MainActivity#result result display}.
         */
        public abstract void postCallback(Handler handler);
    }

    /**
     * initialize all necessary components for generating a flash effect
     * @param mainActivity main application instance
     */
    private FlashEffect(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        this.completed = true;
        this.handler = new Handler(FlashEffect.this.mainActivity.getMainLooper());
    }

    /**
     * obtain the singleton instance of <code>FlashEffect</code> object
     * @param mainActivity main calculator activity
     * @return singleton instance of <code>FlashEffect</code> object
     */
    public static FlashEffect getInstance(MainActivity mainActivity) {
        synchronized (FlashEffect.LOCK) {
            if (instance == null) {
                instance = new FlashEffect(mainActivity);
            }
            return instance;
        }
    }

    /**
     * check if the flash effect is completely rendered. This get operation is thread-safe.
     * @return true if so; otherwise false
     * @see #completed
     */
    public boolean isCompleted() {
        synchronized (FlashEffect.LOCK) {
            return completed;
        }
    }

    /**
     * set the completion state. This set operation is thread-safe.
     * @param completed new completion state
     */
    public void setCompleted(boolean completed) {
        synchronized (FlashEffect.LOCK) {
            this.completed = completed;
        }
    }

    /**
     * run the flash effect on the {@link MainActivity#result result display}
     * @param instance {@link IFlashEffect} instance
     */
    public void run(IFlashEffect instance) {
        if (this.isCompleted()) {
            this.setCompleted(false);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            FlashEffect.this.mainActivity.result.setText("");
                            FlashEffect.this.setCompleted(false);
                        }
                    }, MainActivity.RESULT_SYNC_DURATION);
                    try {
                        Thread.sleep(FlashEffect.FLASH_DURATION);
                    }
                    catch (InterruptedException e) {
                        Log.e(MainActivity.TAG, Log.getStackTraceString(e));
                    }
                    instance.postCallback(handler);
                }
            }).start();
        }
    }
}
