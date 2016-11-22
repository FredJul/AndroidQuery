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

import net.frju.androidquery.database.DatabaseProvider;
import net.frju.androidquery.database.Query;
import net.frju.androidquery.operation.clause.Clause;
import net.frju.androidquery.operation.clause.Where;

import java.util.concurrent.Callable;

import io.reactivex.Observable;

/**
 * Executes an Update query against the SQLite database
 * @author Samuel Kirton [sam@memtrip.com]
 */
public class Update extends Query {
    private Object mModel;
    private ContentValues mContentValues;
    private Clause[] mConditions;

    public Object getModel() {
        return mModel;
    }

    public ContentValues getContentValues() {
        return mContentValues;
    }

    public Clause[] getConditions() {
        return mConditions;
    }

    public Update(Object model) {
        mModel = model;
    }

    public Update(ContentValues contentValues, Clause[] conditions) {
        mContentValues = contentValues;
        mConditions = conditions;
    }

    public static <T> Update.Builder getBuilder(Class<T> classDef, DatabaseProvider databaseProvider) {
        return new Update.Builder<>(classDef, databaseProvider);
    }

    public static class Builder<T> {
        private T mModel;
        private ContentValues mValues;
        private Clause[] mClause;
        private Class<T> mClassDef;
        private DatabaseProvider mDatabaseProvider;

        private Builder(Class<T> classDef, DatabaseProvider databaseProvider) {
            mClassDef = classDef;
            mDatabaseProvider = databaseProvider;
        }

        /**
         * Specify a Where clause for the Update query
         * @param clause Where clause
         * @return Call Builder#execute or Builder#rx to run the query
         */
        public Builder<T> where(Where... clause) {
            mClause = clause;
            return this;
        }

        /**
         * Specify the values for the Update query
         * @param model The model that are being updated
         * @return Call Builder#execute or Builder#rx to run the query
         */
        public Builder<T> model(T model) {
            mModel = model;
            return this;
        }

        /**
         * Specify the values for the Update query
         * @param values The values that are being updated
         * @return Call Builder#execute or Builder#rx to run the query
         */
        public Builder<T> values(ContentValues values) {
            mValues = values;
            return this;
        }

        /**
         * Executes an Update query
         * @return The rows affected by the Update query
         */
        public int execute() {
            if (mModel != null) {
                return update(
                        new Update(mModel),
                        mClassDef,
                        mDatabaseProvider
                );
            } else {
                return update(
                        new Update(mValues, mClause),
                        mClassDef,
                        mDatabaseProvider
                );
            }
        }

        /**
         * Executes an Update query
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