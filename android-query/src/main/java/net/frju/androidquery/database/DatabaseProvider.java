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
package net.frju.androidquery.database;

import android.content.ContentValues;
import android.database.Cursor;

import net.frju.androidquery.operation.condition.Condition;
import net.frju.androidquery.operation.join.Join;
import net.frju.androidquery.operation.keyword.Limit;
import net.frju.androidquery.operation.keyword.OrderBy;

/**
 * @author Samuel Kirton [sam@memtrip.com]
 */
public abstract class DatabaseProvider {
    protected ClauseHelper mClauseHelper;

    protected abstract Resolver getResolver();

    protected DatabaseProvider() {
        mClauseHelper = new ClauseHelper();
    }

    abstract protected int bulkInsert(String tableName, ContentValues[] valuesArray);

    abstract protected int bulkUpdate(String tableName, ContentValues[] valuesArray, Condition[][] conditionsArray);

    abstract protected Cursor query(String tableName, String[] columns, Condition[] condition, Join[] joins,
                                    String groupBy, String having, OrderBy[] orderBy, Limit limit);

    abstract protected int delete(String tableName, Condition[] condition);

    abstract protected long count(String tableName, Condition[] condition);

    abstract protected Cursor rawQuery(String sql);
}