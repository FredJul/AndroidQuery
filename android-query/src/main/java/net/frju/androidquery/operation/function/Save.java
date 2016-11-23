package net.frju.androidquery.operation.function;

import net.frju.androidquery.database.DatabaseProvider;
import net.frju.androidquery.database.Query;

import java.util.concurrent.Callable;

import io.reactivex.Observable;

/**
 * Executes an Save query against the SQLite database. This is similar to Update but also insert the data if not existing.
 * This is quite convenient but also much less efficient than directly doing an update or insert.
 */
public class Save extends Query {
    private Object[] mModels;

    public Object[] getModels() {
        return mModels;
    }

    private Save(Object... models) {
        mModels = models;
    }

    public static <T> Save.Builder getBuilder(DatabaseProvider databaseProvider, T... values) {
        return new Save.Builder<>(databaseProvider, values);
    }

    public static class Builder<T> {
        private T[] mValues;
        private DatabaseProvider mDatabaseProvider;

        private Builder(DatabaseProvider databaseProvider, T... values) {
            mValues = values;
            mDatabaseProvider = databaseProvider;
        }

        /**
         * Executes an Save query
         */
        public int query() {
            return save(
                    new Save(mValues),
                    mValues != null && mValues.length > 0 ? mValues[0].getClass() : Object.class,
                    mDatabaseProvider
            );
        }

        /**
         * Executes an Insert query
         *
         * @return An RxJava Observable
         */
        public Observable<Integer> rx() {
            return wrapRx(new Callable<Integer>() {
                @Override
                public Integer call() throws Exception {
                    return query();
                }
            });
        }
    }
}