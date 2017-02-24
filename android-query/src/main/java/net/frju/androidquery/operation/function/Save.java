package net.frju.androidquery.operation.function;

import android.support.annotation.NonNull;

import net.frju.androidquery.database.DatabaseProvider;
import net.frju.androidquery.database.Query;

import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Single;

/**
 * Executes an Save query against the SQLite database. This isEqualTo similar to Update but also insert the data if not existing.
 * This isEqualTo quite convenient but also much less efficient than directly doing an update or insert.
 */
public class Save extends Query {
    private final Object[] mModels;
    private final ConflictResolution mConflictResolution;

    public Object[] getModels() {
        return mModels;
    }

    public ConflictResolution getConflictResolution() {
        return mConflictResolution;
    }

    private Save(ConflictResolution conflictResolution, Object... models) {
        mConflictResolution = conflictResolution;
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
        private ConflictResolution mConflictResolution = ConflictResolution.CONFLICT_IGNORE;
        private final DatabaseProvider mDatabaseProvider;

        @SafeVarargs
        private Builder(@NonNull DatabaseProvider databaseProvider, @NonNull T... models) {
            mModels = models;
            mDatabaseProvider = databaseProvider;
        }

        /**
         * Specify a Compare where for the Select query
         *
         * @param conflictResolution the resolution algorithm. By default it's CONFLICT_IGNORE.
         * @return Call Builder#query or the rx methods to run the query
         */
        public Save.Builder<T> withConflictResolution(ConflictResolution conflictResolution) {
            mConflictResolution = conflictResolution;
            return this;
        }

        /**
         * Executes an Save query
         * @return the number of inserted or updated items
         */
        public int query() {
            return save(
                    new Save(mConflictResolution, mModels),
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
        rx.Single<Integer> rx() {
            return wrapRx(new Callable<Integer>() {
                @Override
                public Integer call() throws Exception {
                    return query();
                }
            });
        }

        /**
         * Executes an Insert query
         *
         * @return An RxJava2 Observable
         */
        public
        @NonNull
        Single<Integer> rx2() {
            return wrapRx2(new Callable<Integer>() {
                @Override
                public Integer call() throws Exception {
                    return query();
                }
            });
        }
    }
}