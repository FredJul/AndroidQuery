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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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

    protected DatabaseProvider(@NonNull Context context) {
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
    Uri getUri(@NonNull Class model, @Nullable String uriSuffix);

    abstract public
    @NonNull
    Uri getUri(@NonNull String modelDbName, @Nullable String uriSuffix);

    abstract protected long insert(@NonNull String tableName, @NonNull ContentValues valuesArray, @NonNull Query.ConflictResolution conflictResolution);

    abstract protected int bulkInsert(@NonNull String tableName, @NonNull ContentValues[] valuesArray, @NonNull Query.ConflictResolution conflictResolution);

    abstract protected int bulkUpdate(@NonNull String tableName, @Nullable String uriSuffix, @NonNull ContentValues[] valuesArray, @NonNull Where[][] conditionsArray, @NonNull Query.ConflictResolution conflictResolution);

    abstract protected Cursor query(@NonNull String tableName, @NonNull String[] columns, @Nullable Where[] where, @Nullable Join[] joins,
                                    @Nullable String groupBy, @Nullable String having, @Nullable OrderBy[] orderBy, @Nullable Limit limit);

    abstract protected int delete(@NonNull String tableName, @Nullable String uriSuffix, @Nullable Where[] where);

    abstract protected long count(@NonNull String tableName, @Nullable Where[] where);

    abstract protected
    @Nullable
    Cursor rawQuery(@NonNull String sql);

    static
    @NonNull
    String firstToUpperCase(@NonNull String value) {
        return Character.toUpperCase(value.charAt(0)) + value.substring(1);
    }

    static
    @NonNull
    String firstToLowerCase(@NonNull String value) {
        return Character.toLowerCase(value.charAt(0)) + value.substring(1);
    }
}