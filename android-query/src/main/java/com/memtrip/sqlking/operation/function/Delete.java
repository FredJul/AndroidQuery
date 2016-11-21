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
package com.memtrip.sqlking.operation.function;

import com.memtrip.sqlking.database.DatabaseProvider;
import com.memtrip.sqlking.database.Query;
import com.memtrip.sqlking.operation.clause.Clause;

import java.util.concurrent.Callable;

import io.reactivex.Observable;

/**
 * Executes a Delete query against the SQLite database
 * @author Samuel Kirton [sam@memtrip.com]
 */
public class Delete extends Query {
    private Clause[] mConditions;

    public Clause[] getConditions() {
        return mConditions;
    }

    private Delete(Clause[] conditions) {
        mConditions = conditions;
    }

    public static <T> Delete.Builder getBuilder(Class<T> classDef, DatabaseProvider databaseProvider) {
        return new Delete.Builder<>(classDef, databaseProvider);
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
         * Specify a Where clause for the Delete query
         * @param clause Where clause
         * @return Call Builder#execute or Builder#rx to run the query
         */
        public Builder where(Clause... clause) {
            mClause = clause;
            return this;
        }

        /**
         * Executes a Delete query
         * @return The rows affected by the Delete query
         */
        public int execute() {
            return delete(
                    new Delete(mClause),
                    mClassDef,
                    mDatabaseProvider
            );
        }
        /**
         * Executes a Delete query
         * @return An RxJava Observable
         */
        public Observable<Integer> rx() {
            return wrapRx(new Callable<Integer>() {
                @Override
                public Integer call() throws Exception {
                    return execute();
                }
            });
        }
    }
}