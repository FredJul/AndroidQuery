/**
 * Copyright 2013-present memtrip LTD.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.frju.androidquery.operation.function;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import net.frju.androidquery.database.DatabaseProvider;
import net.frju.androidquery.database.Query;
import net.frju.androidquery.operation.condition.Compare;
import net.frju.androidquery.operation.condition.Where;

import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Single;

/**
 * Executes an Update query against the SQLite database
 * @author Samuel Kirton [sam@memtrip.com]
 */
public class Update extends Query {
    private Object[] mModels;
    private ContentValues mContentValues;
    private Where[] mWheres;

    public Object[] getModels() {
        return mModels;
    }

    public ContentValues getContentValues() {
        return mContentValues;
    }

    public Where[] getConditions() {
        return mWheres;
    }

    private Update(Object... models) {
        mModels = models;
    }

    private Update(ContentValues contentValues, Where[] wheres) {
        mContentValues = contentValues;
        mWheres = wheres;
    }

    public static
    @NonNull
    <T> Update.Builder getBuilder(@NonNull Class<T> classDef, @NonNull DatabaseProvider databaseProvider) {
        return new Update.Builder<>(classDef, databaseProvider);
    }

    public static class Builder<T> {
        private T[] mModels;
        private ContentValues mValues;
        private Where[] mWhere;
        private final Class<T> mClassDef;
        private final DatabaseProvider mDatabaseProvider;

        private Builder(@NonNull Class<T> classDef, @NonNull DatabaseProvider databaseProvider) {
            mClassDef = classDef;
            mDatabaseProvider = databaseProvider;
        }

        /**
         * Specify a Compare clause for the Update query
         * @param clause Compare clause
         * @return Call Builder#query or the rx methods to run the query
         */
        public
        @NonNull
        Builder<T> where(Compare... clause) {
            mWhere = clause;
            return this;
        }

        /**
         * Specify the values for the Update query
         * @param models The models that are being updated
         * @return Call Builder#query or the rx methods to run the query
         */
        @SafeVarargs
        public final
        @NonNull
        Builder<T> model(T... models) {
            mModels = models;
            return this;
        }

        /**
         * Specify the values for the Update query
         *
         * @param models The models that are being updated
         * @return Call Builder#query or the rx methods to run the query
         */
        public
        @NonNull
        Builder<T> model(@NonNull List<T> models) {
            //noinspection unchecked,SuspiciousToArrayCall
            mModels = (T[]) models.toArray(new Object[models.size()]);
            return this;
        }

        /**
         * Specify the values for the Update query
         * @param values The values that are being updated
         * @return Call Builder#query or the rx methods to run the query
         */
        public
        @NonNull
        Builder<T> values(ContentValues values) {
            mValues = values;
            return this;
        }

        /**
         * Executes an Update query
         * @return The rows affected by the Update query
         */
        public int query() {
            if (mModels != null) {
                return update(
                        new Update(mModels),
                        mClassDef,
                        mDatabaseProvider
                );
            } else {
                return update(
                        new Update(mValues, mWhere),
                        mClassDef,
                        mDatabaseProvider
                );
            }
        }

        /**
         * Executes an Update query
         * @return An RxJava Observable
         */
        public
        @NonNull
        rx.Single<Integer> rx() {
            return wrapRx(new Callable<Integer>() {
                @Override
                public Integer call() throws Exception {
                    return query();
                }
            });
        }

        /**
         * Executes an Update query
         *
         * @return An RxJava2 Observable
         */
        public
        @NonNull
        Single<Integer> rx2() {
            return wrapRx2(new Callable<Integer>() {
                @Override
                public Integer call() throws Exception {
                    return query();
                }
            });
        }
    }
}