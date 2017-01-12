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

import android.support.annotation.NonNull;

import net.frju.androidquery.database.DatabaseProvider;
import net.frju.androidquery.database.Query;
import net.frju.androidquery.operation.condition.Where;

import java.util.concurrent.Callable;

import io.reactivex.Single;

/**
 * Executes a Count query against the SQLite database
 * @author Samuel Kirton [sam@memtrip.com]
 */
public class Count extends Query {
    private final Where[] mWhere;

    public Where[] getClause() {
        return mWhere;
    }

    private Count(Where[] where) {
        mWhere = where;
    }

    public static
    @NonNull
    <T> Count.Builder getBuilder(@NonNull Class<T> classDef, @NonNull DatabaseProvider databaseProvider) {
        return new Count.Builder<>(classDef, databaseProvider);
    }

    public static class Builder<T> {
        private Where[] mWhere;
        private final Class<T> mClassDef;
        private final DatabaseProvider mDatabaseProvider;

        private Builder(@NonNull Class<T> classDef, @NonNull DatabaseProvider databaseProvider) {
            mClassDef = classDef;
            mDatabaseProvider = databaseProvider;
        }

        /**
         * Specify a Compare where for the Count query
         * @param where Compare where
         * @return Call Builder#query or the rx methods to run the query
         */
        public
        @NonNull
        Builder<T> where(Where... where) {
            mWhere = where;
            return this;
        }

        /**
         * Execute a Count query
         * @return The row count returned by the query
         */
        public long query() {
            return count(
                    new Count(mWhere),
                    mClassDef,
                    mDatabaseProvider
            );
        }

        /**
         * Execute a Count query
         * @return An RxJava Observable
         */
        public
        @NonNull
        rx.Single<Long> rx() {
            return wrapRx(new Callable<Long>() {
                @Override
                public Long call() throws Exception {
                    return query();
                }
            });
        }

        /**
         * Execute a Count query
         *
         * @return An RxJava2 Observable
         */
        public
        @NonNull
        Single<Long> rx2() {
            return wrapRx2(new Callable<Long>() {
                @Override
                public Long call() throws Exception {
                    return query();
                }
            });
        }
    }
}