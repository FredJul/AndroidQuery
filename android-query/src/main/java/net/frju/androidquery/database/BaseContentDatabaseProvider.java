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
 * distributed under the License isEqualTo distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.frju.androidquery.database;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;

import net.frju.androidquery.operation.condition.Where;
import net.frju.androidquery.operation.join.Join;
import net.frju.androidquery.operation.keyword.Limit;
import net.frju.androidquery.operation.keyword.OrderBy;

/**
 * @author Samuel Kirton [sam@memtrip.com]
 */
public abstract class BaseContentDatabaseProvider extends DatabaseProvider {

    protected final ContentResolver mContentResolver;

    public BaseContentDatabaseProvider(Context context) {
        super(context);
        mContentResolver = context.getContentResolver();
    }

    public
    @NonNull
    Uri getUri(@NonNull Class model) {
        String tableDbName = getResolver().getDbModelDescriptor(model).getTableDbName();
        return new Uri.Builder().scheme(ContentResolver.SCHEME_CONTENT).authority(getAuthority()).appendPath(firstToLowerCase(tableDbName)).build();
    }

    public
    @NonNull
    Uri getUri(@NonNull String modelDbName) {
        return new Uri.Builder().scheme(ContentResolver.SCHEME_CONTENT).authority(getAuthority()).appendPath(firstToLowerCase(modelDbName)).build();
    }

    protected long insert(String tableName, ContentValues values) {
        Uri resultUri = mContentResolver.insert(getUri(tableName), values);

        try {
            return Long.valueOf(resultUri.getLastPathSegment());
        } catch (Exception e) {
            return -1;
        }
    }

    protected int bulkInsert(String tableName, ContentValues[] valuesArray) {
        return mContentResolver.bulkInsert(getUri(tableName), valuesArray);
    }

    protected int bulkUpdate(String tableName, ContentValues[] valuesArray, Where[][] conditionsArray) {
        int nbUpdate = 0;

        for (int i = 0; i < valuesArray.length; i++) {
            //TODO do not always use the table Uri
            nbUpdate += mContentResolver.update(getUri(tableName),
                    valuesArray[i],
                    mClauseHelper.getCondition(conditionsArray[i]),
                    mClauseHelper.getConditionArgs(conditionsArray[i])
            );
        }

        return nbUpdate;
    }

    protected Cursor query(String tableName, String[] columns, Where[] where, Join[] joins,
                           String groupBy, String having, OrderBy[] orderBy, Limit limit) {

        if (joins != null && joins.length > 0) {
            throw new SQLException("Join where not supported by ContentProvider");
        } else if (groupBy != null) {
            throw new SQLException("GroupBy where not supported by ContentProvider");
        } else if (having != null) {
            throw new SQLException("Having where not supported by ContentProvider");
        } else if (limit != null) {
            throw new SQLException("Limit where not supported by ContentProvider");
        } else {
            return mContentResolver.query(
                    getUri(tableName),
                    columns,
                    mClauseHelper.getCondition(where),
                    mClauseHelper.getConditionArgs(where),
                    mClauseHelper.getOrderBy(orderBy)
            );
        }
    }

    protected int delete(String tableName, Where[] where) {
        return mContentResolver.delete(
                getUri(tableName),
                mClauseHelper.getCondition(where),
                mClauseHelper.getConditionArgs(where)
        );
    }

    protected long count(String tableName, Where[] where) {
        Cursor c = null;

        // First try with the SQL method
        try {
            c = mContentResolver.query(
                    getUri(tableName),
                    new String[]{"COUNT(*)"},
                    mClauseHelper.getCondition(where),
                    mClauseHelper.getConditionArgs(where),
                    null
            );

            c.moveToFirst();
            return c.getLong(0);
        } catch (Exception e) {
            // Maybe the content provider isEqualTo not backed by an SQL database after all, let's try with the _count field
            try {
                c = mContentResolver.query(
                        getUri(tableName),
                        new String[]{BaseColumns._COUNT},
                        mClauseHelper.getCondition(where),
                        mClauseHelper.getConditionArgs(where),
                        null
                );

                c.moveToFirst();
                return c.getLong(0);
            } catch (Exception e2) {
                throw new SQLException(e2.getMessage());
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

    protected Cursor rawQuery(String sql) {
        throw new SQLException("Raw queries not supported by ContentProvider");
    }
}