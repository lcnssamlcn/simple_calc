package com.example.lcn.calc;

import android.os.Handler;
import android.util.Log;

/**
 * This class helps create a flash effect on the {@link MainActivity#result result display} so that
 * the calculator will be more realistic.
 */
public class FlashEffect {
    /**
     * time duration for the flash effect (in ms)
     */
    public static final long FLASH_DURATION = 100;

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
    private boolean completed;

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
    public FlashEffect(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        this.completed = true;
        this.handler = null;
    }

    /**
     * run the flash effect on the {@link MainActivity#result result display}
     * @param instance {@link IFlashEffect} instance
     */
    public void run(IFlashEffect instance) {
        if (completed) {
            this.completed = false;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (FlashEffect.this.handler == null)
                        handler = new Handler(FlashEffect.this.mainActivity.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            FlashEffect.this.mainActivity.result.setText("");
                        }
                    });
                    try {
                        Thread.sleep(FlashEffect.FLASH_DURATION);
                    }
                    catch (InterruptedException e) {
                        Log.e(MainActivity.TAG, Log.getStackTraceString(e));
                    }
                    instance.postCallback(handler);
                    FlashEffect.this.completed = true;
                }
            }).start();
        }
    }
}
