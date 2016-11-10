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
package com.memtrip.sqlking.database;

import android.content.ContentValues;
import android.database.Cursor;

import com.memtrip.sqlking.common.Resolver;
import com.memtrip.sqlking.operation.clause.Clause;
import com.memtrip.sqlking.operation.join.Join;
import com.memtrip.sqlking.operation.keyword.Limit;
import com.memtrip.sqlking.operation.keyword.OrderBy;

/**
 * @author Samuel Kirton [sam@memtrip.com]
 */
public abstract class DatabaseProvider {
    protected Resolver mResolver;
    protected ClauseHelper mClauseHelper;

    protected Resolver getResolver() {
        return mResolver;
    }

    protected DatabaseProvider(Resolver resolver) {
        mResolver = resolver;
        mClauseHelper = new ClauseHelper();
    }

    abstract protected void bulkInsert(String tableName, ContentValues[] valuesArray);

    abstract protected int update(String tableName, ContentValues values, Clause[] clause);

    abstract protected Cursor query(String tableName, String[] columns, Clause[] clause, Join[] joins,
                                    String groupBy, String having, OrderBy orderBy, Limit limit);

    abstract protected int delete(String tableName, Clause[] clause);

    abstract protected long count(String tableName, Clause[] clause);

    abstract protected Cursor rawQuery(String sql);
}