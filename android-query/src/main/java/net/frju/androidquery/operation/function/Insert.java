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

import java.util.concurrent.Callable;

import io.reactivex.Observable;

/**
 * Executes an Insert query against the SQLite database
 * @author Samuel Kirton [sam@memtrip.com]
 */
public class Insert extends Query {
    private Object[] mModels;

    public Object[] getModels() {
        return mModels;
    }

    private Insert(Object... models) {
        mModels = models;
    }

    public static <T> Insert.Builder getBuilder(DatabaseProvider databaseProvider, T... values) {
        return new Insert.Builder<>(databaseProvider, values);
    }

    public static class Builder<T> {
        private T[] mValues;
        private DatabaseProvider mDatabaseProvider;

        private Builder(DatabaseProvider databaseProvider, T... values) {
            mValues = values;
            mDatabaseProvider = databaseProvider;
        }

        /**
         * Executes an Insert query
         */
        public int query() {
            return insert(
                    new Insert(mValues),
                    mValues != null && mValues.length > 0 ? mValues[0].getClass() : Object.class,
                    mDatabaseProvider
            );
        }

        /**
         * Executes an Insert query
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