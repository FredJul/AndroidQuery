package com.memtrip.sqlking.operation.function;

import android.database.Cursor;

import com.memtrip.sqlking.database.DatabaseProvider;
import com.memtrip.sqlking.database.Query;

import java.util.concurrent.Callable;

import io.reactivex.Observable;

public class Raw extends Query {

    private Raw() {
    }

    public static Builder getBuilder() {
        return new Builder();
    }

    public static class Builder {
        private String mQuery;

        public Builder query(String query) {
            mQuery = query;
            return this;
        }

        public Cursor execute(DatabaseProvider databaseProvider) {
            return rawQuery(mQuery, databaseProvider);
        }

        /**
         * Executes a Row query
         *
         * @param databaseProvider Where the magic happens!
         * @return An RxJava Observable
         */
        public Observable<Cursor> rx(final DatabaseProvider databaseProvider) {
            return wrapRx(new Callable<Cursor>() {
                @Override
                public Cursor call() throws Exception {
                    return execute(databaseProvider);
                }
            });
        }
    }
}
