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
import net.frju.androidquery.operation.join.Join;
import net.frju.androidquery.operation.keyword.Limit;
import net.frju.androidquery.operation.keyword.OrderBy;

import java.util.Arrays;
import java.util.concurrent.Callable;

import io.reactivex.Observable;

/**
 * Executes a Select query against the SQLite database
 * @author Samuel Kirton [sam@memtrip.com]
 */
public class Select extends Query {
    private Clause[] mClause;
    private Join[] mJoin;
    private OrderBy[] mOrderBy;
    private Limit mLimit;

    public Clause[] getClause() {
        return mClause;
    }

    public Join[] getJoin() {
        return mJoin;
    }

    public OrderBy[] getOrderBy() {
        return mOrderBy;
    }

    public Limit getLimit() {
        return mLimit;
    }

    private Select(Clause[] clause, Join[] join, OrderBy[] orderBy, Limit limit) {
        mClause = clause;
        mJoin = join;
        mOrderBy = orderBy;
        mLimit = limit;
    }

    public static <T> Builder getBuilder(Class<T> classDef, DatabaseProvider databaseProvider) {
        return new Builder<>(classDef, databaseProvider);
    }

    public static class Builder<T> {
        private Clause[] mClause;
        private Join[] mJoin;
        private OrderBy[] mOrderBy;
        private Limit mLimit;
        private Class<T> mClassDef;
        private DatabaseProvider mDatabaseProvider;

        private Builder(Class<T> classDef, DatabaseProvider databaseProvider) {
            mClassDef = classDef;
            mDatabaseProvider = databaseProvider;
        }

        /**
         * Specify a Where clause for the Select query
         * @param clause Where clause
         * @return Call Builder#query, Builder#rx, or Builder#rxSingle to run the query
         */
        public Builder<T> where(Clause... clause) {
            mClause = clause;
            return this;
        }

        public Builder<T> join(Join... joins) {
            mJoin = joins;
            return this;
        }

        /**
         * Specify an Order By clause for the Select query
         * @param column The column to use with the Order By clause
         * @param order The direction of the Order By clause
         * @return Call Builder#executem Builder#rx or Builder#rxSingle to run the query
         */
        public Builder<T> orderBy(String column, OrderBy.Order order) {
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
         * @return Call Builder#executem Builder#rx or Builder#rxSingle to run the query
         */
        public Builder<T> orderBy(OrderBy... orderBy) {
            mOrderBy = orderBy;
            return this;
        }

        /**
         * Specify a Limit clause for the Select query
         * @param start The starting index to select from
         * @param end The ending index to select from
         * @return Call Builder#query, Builder#rx or Builder#rxSingle to run the query
         */
        public Builder<T> limit(int start, int end) {
            mLimit = new Limit(start, end);
            return this;
        }

        /**
         * Executes a Select query
         * @return The rows returned by the Select query
         */
        public Result<T> query() {
            return select(
                    new Select(mClause, mJoin, mOrderBy, mLimit),
                    mClassDef,
                    mDatabaseProvider
            );
        }

        /**
         * Executes a Select query that expects a single result
         * @return The row returned by the Select query
         */
        public T querySingle() {
            return selectSingle(
                    new Select(mClause, mJoin, mOrderBy, mLimit),
                    mClassDef,
                    mDatabaseProvider
            );
        }

        /**
         * Executes a Select query
         * @return An RxJava Observable
         */
        public Observable<Result<T>> rx() {
            return wrapRx(new Callable<Result<T>>() {
                @Override
                public Result<T> call() throws Exception {
                    return query();
                }
            });
        }

        /**
         * Executes a Select query that expects a single result
         * @return An RxJava Observable
         */
        public Observable<T> rxSingle() {
            return wrapRx(new Callable<T>() {
                @Override
                public T call() throws Exception {
                    return querySingle();
                }
            });
        }
    }
}