package net.frju.androidquery.database;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Loader;

import net.frju.androidquery.operation.function.Result;

public abstract class BaseSelectLoader<T> extends AsyncTaskLoader<Result<T>> {

    private final Loader.ForceLoadContentObserver mObserver = new Loader.ForceLoadContentObserver();
    private Result<T> mResult;

    public BaseSelectLoader(Context context) {
        super(context);
    }

    @Override
    public Result<T> loadInBackground() {
        Result<T> cursor = doSelect();

        if (cursor != null) {
            // Ensure the cursor window is filled
            cursor.getCount();
            cursor.registerContentObserver(mObserver);
        }

        return cursor;
    }

    @Override
    public void deliverResult(Result<T> data) {
        if (isReset()) {
            // An async query came in while the loader is stopped
            return;
        }

        this.mResult = data;

        super.deliverResult(data);
    }

    @Override
    protected void onStartLoading() {
        if (mResult != null) {
            deliverResult(mResult);
        }

        if (takeContentChanged() || mResult == null) {
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

        mResult = null;
    }

    public abstract Result<T> doSelect();
}
