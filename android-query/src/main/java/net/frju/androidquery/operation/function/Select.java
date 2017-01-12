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
import net.frju.androidquery.operation.join.Join;
import net.frju.androidquery.operation.keyword.Limit;
import net.frju.androidquery.operation.keyword.OrderBy;

import java.util.Arrays;
import java.util.concurrent.Callable;

import io.reactivex.Single;

/**
 * Executes a Select query against the SQLite database
 * @author Samuel Kirton [sam@memtrip.com]
 */
public class Select extends Query {
    private final Where[] mWhere;
    private final Join[] mJoins;
    private final OrderBy[] mOrderBy;
    private final Limit mLimit;

    public Where[] getClause() {
        return mWhere;
    }

    public Join[] getJoins() {
        return mJoins;
    }

    public OrderBy[] getOrderBy() {
        return mOrderBy;
    }

    public Limit getLimit() {
        return mLimit;
    }

    private Select(Where[] where, Join[] join, OrderBy[] orderBy, Limit limit) {
        mWhere = where;
        mJoins = join;
        mOrderBy = orderBy;
        mLimit = limit;
    }

    public static
    @NonNull
    <T> Builder getBuilder(@NonNull Class<T> classDef, @NonNull DatabaseProvider databaseProvider) {
        return new Builder<>(classDef, databaseProvider);
    }

    public static class Builder<T> {
        private Where[] mWhere;
        private Join[] mJoins;
        private OrderBy[] mOrderBy;
        private Limit mLimit;
        private final Class<T> mClassDef;
        private final DatabaseProvider mDatabaseProvider;

        private Builder(@NonNull Class<T> classDef, @NonNull DatabaseProvider databaseProvider) {
            mClassDef = classDef;
            mDatabaseProvider = databaseProvider;
        }

        /**
         * Specify a Compare where for the Select query
         * @param where Compare where
         * @return Call Builder#query or the rx methods to run the query
         */
        public Builder<T> where(Where... where) {
            mWhere = where;
            return this;
        }

        public
        @NonNull
        Builder<T> join(Join... joins) {
            mJoins = joins;
            return this;
        }

        /**
         * Specify an Order By clause for the Select query
         * @param column The column to use with the Order By clause
         * @param order The direction of the Order By clause
         * @return Call Builder#query or the rx methods to run the query
         */
        public
        @NonNull
        Builder<T> orderBy(@NonNull String column, @NonNull OrderBy.Order order) {
            if (mOrderBy == null) {
                mOrderBy = new OrderBy[]{new OrderBy(column, order)};
            } else {
                mOrderBy = Arrays.copyOf(mOrderBy, mOrderBy.length + 1);
                mOrderBy[mOrderBy.length - 1] = new OrderBy(column, order);
            }
            return this;
        }

        /**
         * Specify an Order By clause for the Select query
         *
         * @param orderBy the list of order of
         * @return Call Builder#query or the rx methods to run the query
         */
        public
        @NonNull
        Builder<T> orderBy(OrderBy... orderBy) {
            mOrderBy = orderBy;
            return this;
        }

        /**
         * Specify a Limit clause for the Select query
         * @param n The number of wanted results
         * @return Call Builder#query or the rx methods to run the query
         */
        public
        @NonNull
        Builder<T> limit(int n) {
            mLimit = new Limit(0, n);
            return this;
        }

        /**
         * Specify a Limit clause for the Select query
         * @param start The starting index to select from
         * @param end The ending index to select from
         * @return Call Builder#query or the rx methods to run the query
         */
        public
        @NonNull
        Builder<T> limit(int start, int end) {
            mLimit = new Limit(start, end);
            return this;
        }

        /**
         * Executes a Select query
         * @return The rows returned by the Select query
         */
        public
        @NonNull
        CursorResult<T> query() {
            return select(
                    new Select(mWhere, mJoins, mOrderBy, mLimit),
                    mClassDef,
                    mDatabaseProvider
            );
        }

        /**
         * Executes a Select query that expects a single result
         * @return The row returned by the Select query
         */
        public T queryFirst() {
            // For a single query, always put a limit for performance reasons
            return selectFirst(
                    new Select(mWhere, mJoins, mOrderBy, new Limit(0, 1)),
                    mClassDef,
                    mDatabaseProvider
            );
        }

        /**
         * Executes a Select query with all initialization done (including sub queries)
         * @return The rows returned by the Select query
         */
        public T[] queryAndInit() {
            return selectAndInit(
                    new Select(mWhere, mJoins, mOrderBy, mLimit),
                    mClassDef,
                    mDatabaseProvider
            );
        }

        /**
         * Executes a Select query that expects a single result with all initialization done (including sub queries)
         *
         * @return The row returned by the Select query
         */
        public T queryFirstAndInit() {
            // For a single query, always put a limit for performance reasons
            return selectFirstAndInit(
                    new Select(mWhere, mJoins, mOrderBy, new Limit(0, 1)),
                    mClassDef,
                    mDatabaseProvider
            );
        }

        /**
         * Executes a Select query
         * @return An RxJava Observable
         */
        public
        @NonNull
        rx.Single<CursorResult<T>> rx() {
            return wrapRx(new Callable<CursorResult<T>>() {
                @Override
                public CursorResult<T> call() throws Exception {
                    return query();
                }
            });
        }

        /**
         * Executes a Select query with all initialization done (including sub queries)
         *
         * @return An RxJava Observable
         */
        public
        @NonNull
        rx.Single<T[]> rxAndInit() {
            return wrapRx(new Callable<T[]>() {
                @Override
                public T[] call() throws Exception {
                    return queryAndInit();
                }
            });
        }

        /**
         * Executes a Select query that expects a single result
         * @return An RxJava Observable
         */
        public
        @NonNull
        rx.Single<T> rxFirst() {
            return wrapRx(new Callable<T>() {
                @Override
                public T call() throws Exception {
                    return queryFirst();
                }
            });
        }

        /**
         * Executes a Select query that expects a single result with all initialization done (including sub queries)
         *
         * @return An RxJava Observable
         */
        public
        @NonNull
        rx.Single<T> rxFirstAndInit() {
            return wrapRx(new Callable<T>() {
                @Override
                public T call() throws Exception {
                    return queryFirstAndInit();
                }
            });
        }

        /**
         * Executes a Select query
         *
         * @return An RxJava2 Observable
         */
        public
        @NonNull
        Single<CursorResult<T>> rx2() {
            return wrapRx2(new Callable<CursorResult<T>>() {
                @Override
                public CursorResult<T> call() throws Exception {
                    return query();
                }
            });
        }

        /**
         * Executes a Select query with all initialization done (including sub queries)
         *
         * @return An RxJava2 Observable
         */
        public
        @NonNull
        Single<T[]> rx2AndInit() {
            return wrapRx2(new Callable<T[]>() {
                @Override
                public T[] call() throws Exception {
                    return queryAndInit();
                }
            });
        }

        /**
         * Executes a Select query that expects a single result
         *
         * @return An RxJava2 Observable
         */
        public
        @NonNull
        Single<T> rx2First() {
            return wrapRx2(new Callable<T>() {
                @Override
                public T call() throws Exception {
                    return queryFirst();
                }
            });
        }

        /**
         * Executes a Select query that expects a single result with all initialization done (including sub queries)
         *
         * @return An RxJava2 Observable
         */
        public
        @NonNull
        Single<T> rx2FirstAndInit() {
            return wrapRx2(new Callable<T>() {
                @Override
                public T call() throws Exception {
                    return queryFirstAndInit();
                }
            });
        }
    }
}