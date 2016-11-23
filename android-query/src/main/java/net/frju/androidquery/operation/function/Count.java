/**
 * Copyright 2013-present memtrip LTD.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.frju.androidquery.operation.function;

import net.frju.androidquery.database.DatabaseProvider;
import net.frju.androidquery.database.Query;
import net.frju.androidquery.operation.clause.Clause;

import java.util.concurrent.Callable;

import io.reactivex.Observable;

/**
 * Executes a Count query against the SQLite database
 * @author Samuel Kirton [sam@memtrip.com]
 */
public class Count extends Query {
    private Clause[] mClause;

    public Clause[] getClause() {
        return mClause;
    }

    private Count(Clause[] clause) {
        mClause = clause;
    }

    public static <T> Count.Builder getBuilder(Class<T> classDef, DatabaseProvider databaseProvider) {
        return new Count.Builder<>(classDef, databaseProvider);
    }

    public static class Builder<T> {
        private Clause[] mClause;
        private Class<T> mClassDef;
        private DatabaseProvider mDatabaseProvider;

        private Builder(Class<T> classDef, DatabaseProvider databaseProvider) {
            mClassDef = classDef;
            mDatabaseProvider = databaseProvider;
        }

        /**
         * Specify a Where clause for the Count query
         * @param clause Where clause
         * @return Call Builder#query or Builder#rx to run the query
         */
        public Builder<T> where(Clause... clause) {
            mClause = clause;
            return this;
        }

        /**
         * Execute a Count query
         * @return The row count returned by the query
         */
        public long query() {
            return count(
                    new Count(mClause),
                    mClassDef,
                    mDatabaseProvider
            );
        }

        /**
         * Execute a Count query
         * @return An RxJava Observable
         */
        public Observable<Long> rx() {
            return wrapRx(new Callable<Long>() {
                @Override
                public Long call() throws Exception {
                    return query();
                }
            });
        }
    }
}