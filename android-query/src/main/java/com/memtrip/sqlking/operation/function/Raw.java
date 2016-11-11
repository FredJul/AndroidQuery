package com.memtrip.sqlking.operation.function;

import android.database.Cursor;

import com.memtrip.sqlking.database.DatabaseProvider;
import com.memtrip.sqlking.database.Query;

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
    }
}
