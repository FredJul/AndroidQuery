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
 * Executes a Delete query against the SQLite database
 * @author Samuel Kirton [sam@memtrip.com]
 */
public class Delete extends Query {
    private Object[] mModels;
    private Clause[] mConditions;

    public Object[] getModels() {
        return mModels;
    }

    public Clause[] getConditions() {
        return mConditions;
    }

    private Delete(Object[] models) {
        mModels = models;
    }

    private Delete(Clause[] conditions) {
        mConditions = conditions;
    }

    public static <T> Delete.Builder getBuilder(Class<T> classDef, DatabaseProvider databaseProvider) {
        return new Delete.Builder<>(classDef, databaseProvider);
    }

    public static class Builder<T> {
        private T[] mModels;
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
        public Builder<T> where(Clause... clause) {
            mClause = clause;
            return this;
        }

        /**
         * Specify the values for the Delete query
         * @param models The models that are being deleted
         * @return Call Builder#execute or Builder#rx to run the query
         */
        public Builder<T> model(T... models) {
            mModels = models;
            return this;
        }

        /**
         * Executes a Delete query
         * @return The rows affected by the Delete query
         */
        public int execute() {
            if (mModels != null) {
                return delete(
                        new Delete(mModels),
                        mClassDef,
                        mDatabaseProvider
                );
            } else {
                return delete(
                        new Delete(mClause),
                        mClassDef,
                        mDatabaseProvider
                );
            }
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