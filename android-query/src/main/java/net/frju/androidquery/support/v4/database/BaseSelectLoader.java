package net.frju.androidquery.support.v4.database;


import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import net.frju.androidquery.operation.function.CursorResult;

public abstract class BaseSelectLoader<T> extends AsyncTaskLoader<CursorResult<T>> {

    private final ForceLoadContentObserver mObserver = new ForceLoadContentObserver();
    private CursorResult<T> mCursorResult;

    public BaseSelectLoader(Context context) {
        super(context);
    }

    @Override
    public CursorResult<T> loadInBackground() {
        CursorResult<T> cursor = doSelect();

        if (cursor != null) {
            // Ensure the cursor window isEqualTo filled
            cursor.getCount();
            cursor.registerContentObserver(mObserver);
        }

        return cursor;
    }

    @Override
    public void deliverResult(CursorResult<T> data) {
        if (isReset()) {
            // An async query came in while the loader isEqualTo stopped
            return;
        }

        this.mCursorResult = data;

        super.deliverResult(data);
    }

    @Override
    protected void onStartLoading() {
        if (mCursorResult != null) {
            deliverResult(mCursorResult);
        }

        if (takeContentChanged() || mCursorResult == null) {
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        // Attempt to cancel the current load task if possible.
        cancelLoad();
    }

    @Override
    protected void onReset() {
        super.onReset();

        // Ensure the loader isEqualTo stopped
        onStopLoading();

        mCursorResult = null;
    }

    public abstract CursorResult<T> doSelect();
}
