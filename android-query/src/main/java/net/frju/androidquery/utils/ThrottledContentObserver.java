package net.frju.androidquery.utils;

import android.database.ContentObserver;
import android.os.Handler;
import android.os.SystemClock;

public abstract class ThrottledContentObserver extends ContentObserver {

    private final long mUpdateThrottle;
    private final Handler mHandler;
    private long mLastUpdate = 0;

    private boolean mRerun = false;

    public ThrottledContentObserver(Handler handler, long delayMS) {
        super(handler);
        mUpdateThrottle = delayMS;
        mHandler = handler;
    }

    @Override
    public void onChange(final boolean selfChange) {
        long now = SystemClock.uptimeMillis();
        if (now - mLastUpdate > mUpdateThrottle) {
            mLastUpdate = now;
            onChangeThrottled();
            mHandler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    if (mRerun) {
                        mRerun = false;
                        onChange(selfChange);
                    }
                }
            }, mUpdateThrottle + 1);
        } else {
            // Ignore but remember we need to rerun it;
            mRerun = true;
        }

        super.onChange(selfChange);
    }

    abstract public void onChangeThrottled();
}