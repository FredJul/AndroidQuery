package net.frju.androidquery.operation.function;

import android.database.Cursor;

import net.frju.androidquery.database.DatabaseProvider;
import net.frju.androidquery.database.Query;

import java.util.concurrent.Callable;

import io.reactivex.Observable;

public class Raw extends Query {

    private Raw() {
    }

    public static Builder getBuilder(DatabaseProvider databaseProvider, String query) {
        return new Builder(databaseProvider, query);
    }

    public static class Builder {
        private final String mQuery;
        private final DatabaseProvider mDatabaseProvider;

        private Builder(DatabaseProvider databaseProvider, String query) {
            mQuery = query;
            mDatabaseProvider = databaseProvider;
        }

        public Cursor query() {
            return rawQuery(mQuery, mDatabaseProvider);
        }

        /**
         * Executes a Row query
         *
         * @return An RxJava Observable
         */
        public Observable<Cursor> rx() {
            return wrapRx(new Callable<Cursor>() {
                @Override
                public Cursor call() throws Exception {
                    return query();
                }
            });
        }
    }
}
