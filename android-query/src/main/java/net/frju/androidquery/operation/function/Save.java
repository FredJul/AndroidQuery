package net.frju.androidquery.operation.function;

import android.support.annotation.NonNull;

import net.frju.androidquery.database.DatabaseProvider;
import net.frju.androidquery.database.Query;

import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Observable;

/**
 * Executes an Save query against the SQLite database. This is similar to Update but also insert the data if not existing.
 * This is quite convenient but also much less efficient than directly doing an update or insert.
 */
public class Save extends Query {
    private final Object[] mModels;

    public Object[] getModels() {
        return mModels;
    }

    private Save(Object... models) {
        mModels = models;
    }

    @SafeVarargs
    public static
    @NonNull
    <T> Save.Builder getBuilder(@NonNull DatabaseProvider databaseProvider, @NonNull T... models) {
        return new Save.Builder<>(databaseProvider, models);
    }

    public static
    @NonNull
    <T> Save.Builder getBuilder(@NonNull DatabaseProvider databaseProvider, @NonNull List<T> models) {
        //noinspection unchecked,SuspiciousToArrayCall
        return new Save.Builder<>(databaseProvider, (T[]) models.toArray(new Object[models.size()]));
    }

    public static class Builder<T> {
        private final T[] mModels;
        private final DatabaseProvider mDatabaseProvider;

        @SafeVarargs
        private Builder(@NonNull DatabaseProvider databaseProvider, @NonNull T... models) {
            mModels = models;
            mDatabaseProvider = databaseProvider;
        }

        /**
         * Executes an Save query
         */
        public int query() {
            return save(
                    new Save(mModels),
                    mModels != null && mModels.length > 0 ? mModels[0].getClass() : Object.class,
                    mDatabaseProvider
            );
        }

        /**
         * Executes an Insert query
         *
         * @return An RxJava Observable
         */
        public
        @NonNull
        Observable<Integer> rx() {
            return wrapRx(new Callable<Integer>() {
                @Override
                public Integer call() throws Exception {
                    return query();
                }
            });
        }
    }
}