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
import android.support.annotation.Nullable;

import net.frju.androidquery.operation.condition.Where;
import net.frju.androidquery.operation.join.Join;
import net.frju.androidquery.operation.keyword.Limit;
import net.frju.androidquery.operation.keyword.OrderBy;

/**
 * @author Samuel Kirton [sam@memtrip.com]
 */
public abstract class BaseContentDatabaseProvider extends DatabaseProvider {

    protected final ContentResolver mContentResolver;

    public BaseContentDatabaseProvider(@NonNull Context context) {
        super(context);
        mContentResolver = context.getContentResolver();
    }

    @Override
    public
    @NonNull
    Uri getUri(@NonNull Class model, @Nullable String uriSuffix) {
        String tableDbName = getResolver().getDbModelDescriptor(model).getTableDbName();
        if (uriSuffix == null) {
            return new Uri.Builder().scheme(ContentResolver.SCHEME_CONTENT).authority(getAuthority()).appendEncodedPath(firstToLowerCase(tableDbName)).build();
        } else {
            return new Uri.Builder().scheme(ContentResolver.SCHEME_CONTENT).authority(getAuthority()).appendEncodedPath(firstToLowerCase(tableDbName)).appendPath(uriSuffix).build();
        }
    }

    @Override
    public
    @NonNull
    Uri getUri(@NonNull String modelDbName, @Nullable String uriSuffix) {
        if (uriSuffix == null) {
            return new Uri.Builder().scheme(ContentResolver.SCHEME_CONTENT).authority(getAuthority()).appendEncodedPath(firstToLowerCase(modelDbName)).build();
        } else {
            return new Uri.Builder().scheme(ContentResolver.SCHEME_CONTENT).authority(getAuthority()).appendEncodedPath(firstToLowerCase(modelDbName)).appendPath(uriSuffix).build();
        }
    }

    @Override
    protected long insert(@NonNull String tableName, @NonNull ContentValues values, @NonNull Query.ConflictResolution conflictResolution) {
        Uri resultUri = mContentResolver.insert(getUri(tableName, null), values);

        try {
            return Long.valueOf(resultUri.getLastPathSegment());
        } catch (Exception e) {
            return -1;
        }
    }

    @Override
    protected int bulkInsert(@NonNull String tableName, @NonNull ContentValues[] valuesArray, @NonNull Query.ConflictResolution conflictResolution) {
        return mContentResolver.bulkInsert(getUri(tableName, null), valuesArray);
    }

    @Override
    protected int bulkUpdate(@NonNull String tableName, @Nullable String uriSuffix, @NonNull ContentValues[] valuesArray, @NonNull Where[][] conditionsArray, @NonNull Query.ConflictResolution conflictResolution) {
        int nbUpdate = 0;

        for (int i = 0; i < valuesArray.length; i++) {
            nbUpdate += mContentResolver.update(getUri(tableName, uriSuffix),
                    valuesArray[i],
                    mClauseHelper.getCondition(conditionsArray[i]),
                    mClauseHelper.getConditionArgs(conditionsArray[i])
            );
        }

        return nbUpdate;
    }

    @Override
    protected
    @Nullable
    Cursor query(@NonNull String tableName, @NonNull String[] columns, @Nullable Where[] where, @Nullable Join[] joins,
                 @Nullable String groupBy, @Nullable String having, @Nullable OrderBy[] orderBy, @Nullable Limit limit) {

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
                    getUri(tableName, null),
                    columns,
                    mClauseHelper.getCondition(where),
                    mClauseHelper.getConditionArgs(where),
                    mClauseHelper.getOrderBy(orderBy)
            );
        }
    }

    @Override
    protected int delete(@NonNull String tableName, @Nullable String uriSuffix, @Nullable Where[] where) {
        return mContentResolver.delete(
                getUri(tableName, uriSuffix),
                mClauseHelper.getCondition(where),
                mClauseHelper.getConditionArgs(where)
        );
    }

    @Override
    protected long count(@NonNull String tableName, @Nullable Where[] where) {
        Cursor c = null;

        // First try with the SQL method
        try {
            c = mContentResolver.query(
                    getUri(tableName, null),
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
                        getUri(tableName, null),
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

    @Override
    protected
    @Nullable
    Cursor rawQuery(@NonNull String sql) {
        throw new SQLException("Raw queries not supported by ContentProvider");
    }
}