package net.frju.androidquery.database;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Loader;

import net.frju.androidquery.operation.function.CursorResult;

public abstract class BaseSelectLoader<T> extends AsyncTaskLoader<CursorResult<T>> {

    private final Loader.ForceLoadContentObserver mObserver = new Loader.ForceLoadContentObserver();
    private CursorResult<T> mCursorResult;

    public BaseSelectLoader(Context context) {
        super(context);
    }

    @Override
    public CursorResult<T> loadInBackground() {
        CursorResult<T> cursor = doSelect();

        if (cursor != null) {
            // Ensure the cursor window is filled
            cursor.getCount();
            cursor.registerContentObserver(mObserver);
        }

        return cursor;
    }

    @Override
    public void deliverResult(CursorResult<T> data) {
        if (isReset()) {
            // An async query came in while the loader is stopped
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

        // Ensure the loader is stopped
        onStopLoading();

        mCursorResult = null;
    }

    public abstract CursorResult<T> doSelect();
}
