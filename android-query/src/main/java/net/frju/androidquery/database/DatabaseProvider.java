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
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import net.frju.androidquery.operation.condition.Where;
import net.frju.androidquery.operation.join.Join;
import net.frju.androidquery.operation.keyword.Limit;
import net.frju.androidquery.operation.keyword.OrderBy;

/**
 * @author Samuel Kirton [sam@memtrip.com]
 */
public abstract class DatabaseProvider {
    protected final Context mContext;
    protected final ClauseHelper mClauseHelper;

    protected DatabaseProvider(Context context) {
        mContext = context;
        mClauseHelper = new ClauseHelper();
    }

    protected abstract
    @NonNull
    Resolver getResolver();

    protected abstract
    @NonNull
    String getAuthority();

    abstract public
    @NonNull
    Uri getUri(@NonNull Class model);

    abstract public
    @NonNull
    Uri getUri(@NonNull String modelDbName);

    abstract protected long insert(String tableName, ContentValues valuesArray);

    abstract protected int bulkInsert(String tableName, ContentValues[] valuesArray);

    abstract protected int bulkUpdate(String tableName, ContentValues[] valuesArray, Where[][] conditionsArray);

    abstract protected Cursor query(String tableName, String[] columns, Where[] where, Join[] joins,
                                    String groupBy, String having, OrderBy[] orderBy, Limit limit);

    abstract protected int delete(String tableName, Where[] where);

    abstract protected long count(String tableName, Where[] where);

    abstract protected Cursor rawQuery(String sql);

    static String firstToUpperCase(String value) {
        return Character.toUpperCase(value.charAt(0)) + value.substring(1);
    }

    static String firstToLowerCase(String value) {
        return Character.toLowerCase(value.charAt(0)) + value.substring(1);
    }
}