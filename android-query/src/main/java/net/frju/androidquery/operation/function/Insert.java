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
 * distributed under the License isEqualTo distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.frju.androidquery.operation.function;

import android.support.annotation.NonNull;

import net.frju.androidquery.database.DatabaseProvider;
import net.frju.androidquery.database.Query;

import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Single;

/**
 * Executes an Insert query against the SQLite database
 * @author Samuel Kirton [sam@memtrip.com]
 */
public class Insert extends Query {
    private final Object[] mModels;

    public Object[] getModels() {
        return mModels;
    }

    private Insert(Object... models) {
        mModels = models;
    }

    @SafeVarargs
    public static
    @NonNull
    <T> Insert.Builder getBuilder(@NonNull DatabaseProvider databaseProvider, @NonNull T... models) {
        return new Insert.Builder<>(databaseProvider, models);
    }

    public static
    @NonNull
    <T> Insert.Builder getBuilder(@NonNull DatabaseProvider databaseProvider, @NonNull List<T> models) {
        //noinspection unchecked,SuspiciousToArrayCall
        return new Insert.Builder<>(databaseProvider, (T[]) models.toArray(new Object[models.size()]));
    }

    public static class Builder<T> {
        private final T[] mModels;
        private final DatabaseProvider mDatabaseProvider;

        @SafeVarargs
        private Builder(@NonNull DatabaseProvider databaseProvider, @NonNull T... models) {
            mModels = models;
            mDatabaseProvider = databaseProvider;
        }

        /**
         * Executes an Insert query
         */
        public int query() {
            return insert(
                    new Insert(mModels),
                    mModels != null && mModels.length > 0 ? mModels[0].getClass() : Object.class,
                    mDatabaseProvider
            );
        }

        /**
         * Executes an Insert query
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
         * Executes an Insert query
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