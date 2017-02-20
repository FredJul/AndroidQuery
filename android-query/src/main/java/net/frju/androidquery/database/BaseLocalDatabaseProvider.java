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

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import net.frju.androidquery.operation.condition.Where;
import net.frju.androidquery.operation.join.Join;
import net.frju.androidquery.operation.keyword.Limit;
import net.frju.androidquery.operation.keyword.OrderBy;

/**
 * @author Samuel Kirton [sam@memtrip.com]
 */
public abstract class BaseLocalDatabaseProvider extends DatabaseProvider {

    private final SQLiteDatabase mDatabase;
    private final String[] mSchemaArray;
    private final String[][] mColumnsSqlArray;
    private final String[] mTableRealNameArray;
    private final String[] mCreateIndexQuery;

    private class DbInitFeedback {
        boolean onCreateCalled = false;
        boolean onDowngradeCalled = false;
        boolean onUpgradeCalled = false;
        int oldVersion;
        int newVersion;
    }

    public BaseLocalDatabaseProvider(@NonNull Context context) {
        super(context);

        Class<?> modelClassDef[] = getResolver().getModelsForProvider(this.getClass());
        int modelCount = modelClassDef.length;

        mSchemaArray = new String[modelCount];
        mTableRealNameArray = new String[modelCount];
        mColumnsSqlArray = new String[modelCount][];
        mCreateIndexQuery = new String[modelCount];

        for (int i = 0; i < modelClassDef.length; i++) {
            DbModelDescriptor dbModelDescriptor = getResolver().getDbModelDescriptor(modelClassDef[i]);
            mSchemaArray[i] = dbModelDescriptor.getTableCreateQuery();
            mColumnsSqlArray[i] = dbModelDescriptor.getColumnsSqlArray();
            mTableRealNameArray[i] = dbModelDescriptor.getTableDbName();
            mCreateIndexQuery[i] = dbModelDescriptor.getCreateIndexQuery();
        }

        final DbInitFeedback dbInitFeedback = new DbInitFeedback();

        SQLiteOpenHelper openHelper = new SQLiteOpenHelper(context, getDbName(), null, getDbVersion()) {
            @Override
            public void onCreate(SQLiteDatabase db) {
                BaseLocalDatabaseProvider.this.onCreate(db);
                dbInitFeedback.onCreateCalled = true;
            }

            @Override
            public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                BaseLocalDatabaseProvider.this.onDowngrade(db, oldVersion, newVersion);
                dbInitFeedback.onDowngradeCalled = true;
                dbInitFeedback.oldVersion = oldVersion;
                dbInitFeedback.newVersion = newVersion;
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                BaseLocalDatabaseProvider.this.onUpgrade(db, oldVersion, newVersion);
                dbInitFeedback.onUpgradeCalled = true;
                dbInitFeedback.oldVersion = oldVersion;
                dbInitFeedback.newVersion = newVersion;
            }
        };

        //TODO should handle error cases and notably the corrupted database one: we could reconstruct it
        mDatabase = openHelper.getWritableDatabase();

        // We call that after the creation of the database to be able to call AndroidQuery methods in here
        if (dbInitFeedback.onCreateCalled) {
            new Handler(mContext.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    BaseLocalDatabaseProvider.this.onPostCreate();
                }
            });
        } else if (dbInitFeedback.onDowngradeCalled) {
            new Handler(mContext.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    BaseLocalDatabaseProvider.this.onPostDowngrade(dbInitFeedback.oldVersion, dbInitFeedback.newVersion);
                }
            });
        } else if (dbInitFeedback.onUpgradeCalled) {
            new Handler(mContext.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    BaseLocalDatabaseProvider.this.onPostUpgrade(dbInitFeedback.oldVersion, dbInitFeedback.newVersion);
                }
            });
        }
    }

    @Override
    protected
    @NonNull
    String getAuthority() {
        return getClass().getPackage().getName();
    }

    public
    @NonNull
    Uri getUri(@NonNull Class model, @Nullable String uriSuffix) {
        String tableName = getResolver().getDbModelDescriptor(model).getTableDbName();
        if (uriSuffix == null) {
            return new Uri.Builder().scheme(ContentResolver.SCHEME_CONTENT).authority(getAuthority()).appendPath(firstToLowerCase(tableName)).build();
        } else {
            return new Uri.Builder().scheme(ContentResolver.SCHEME_CONTENT).authority(getAuthority()).appendPath(firstToLowerCase(tableName)).appendPath(uriSuffix).build();
        }
    }

    public
    @NonNull
    Uri getUri(@NonNull String modelDbName, @Nullable String uriSuffix) {
        if (uriSuffix == null) {
            return new Uri.Builder().scheme(ContentResolver.SCHEME_CONTENT).authority(getAuthority()).appendPath(firstToLowerCase(modelDbName)).build();
        } else {
            return new Uri.Builder().scheme(ContentResolver.SCHEME_CONTENT).authority(getAuthority()).appendPath(firstToLowerCase(modelDbName)).appendPath(uriSuffix).build();
        }
    }

    protected abstract
    @NonNull
    String getDbName();

    protected abstract int getDbVersion();

    protected void onCreate(@NonNull SQLiteDatabase db) {
        for (String schema : mSchemaArray) {
            db.execSQL(schema);
        }

        for (String createIndex : mCreateIndexQuery) {
            if (createIndex != null) {
                db.execSQL(createIndex);
            }
        }
    }

    protected void onPostCreate() {
    }

    protected void onDowngrade(@NonNull SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    protected void onPostDowngrade(int oldVersion, int newVersion) {
    }

    protected void onUpgrade(@NonNull SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {

            db.beginTransaction();

            // Try to create new table
            for (String schema : mSchemaArray) {
                try {
                    db.execSQL(schema);
                } catch (SQLException e) {
                    // table already exists, nothing to do
                }
            }

            // Try to create new index
            for (String createIndex : mCreateIndexQuery) {
                try {
                    if (createIndex != null) {
                        db.execSQL(createIndex);
                    }
                } catch (SQLException e) {
                    // index already exists, nothing to do
                }
            }

            for (int i = 0; i < mTableRealNameArray.length; i++) {
                String tableName = mTableRealNameArray[i];

                // Try to create new columns
                for (String columnsSql : mColumnsSqlArray[i]) {
                    try {
                        db.execSQL("ALTER TABLE " + tableName + " ADD COLUMN " + columnsSql + ";");
                    } catch (SQLException e) {
                        // column already exists, nothing to do
                    }
                }

                //TODO also add others new constraints, ...
            }

            db.setTransactionSuccessful();
            db.endTransaction();
        }
    }

    protected void onPostUpgrade(int oldVersion, int newVersion) {
    }

    protected long insert(@NonNull String tableName, @NonNull ContentValues values) {
        long newId = mDatabase.insert(tableName, null, values);

        if (newId != -1) {
            mContext.getContentResolver().notifyChange(getUri(tableName, null), null);
        }

        return newId;
    }

    protected int bulkInsert(@NonNull String tableName, @NonNull ContentValues[] valuesArray) {
        int nbInsert = 0;
        mDatabase.beginTransaction();

        try {
            for (ContentValues values : valuesArray) {
                if (mDatabase.insert(tableName, null, values) != -1) {
                    nbInsert++;
                }
            }

            mDatabase.setTransactionSuccessful();
        } finally {
            mDatabase.endTransaction();
        }

        if (nbInsert > 0) {
            mContext.getContentResolver().notifyChange(getUri(tableName, null), null);
        }

        return nbInsert;
    }

    protected int bulkUpdate(@NonNull String tableName, @Nullable String uriSuffix, @NonNull ContentValues[] valuesArray, @NonNull Where[][] conditionsArray) {
        int nbUpdate = 0;
        mDatabase.beginTransaction();

        try {
            for (int i = 0; i < valuesArray.length; i++) {
                nbUpdate += mDatabase.update(
                        tableName,
                        valuesArray[i],
                        mClauseHelper.getCondition(conditionsArray[i]),
                        mClauseHelper.getConditionArgs(conditionsArray[i])
                );
            }

            mDatabase.setTransactionSuccessful();
        } finally {
            mDatabase.endTransaction();
        }

        if (nbUpdate > 0) {
            mContext.getContentResolver().notifyChange(getUri(tableName, uriSuffix), null);
        }

        return nbUpdate;
    }

    protected
    @Nullable
    Cursor query(@NonNull String tableName, @NonNull String[] columns, @Nullable Where[] where, @Nullable Join[] joins,
                 @Nullable String groupBy, @Nullable String having, @Nullable OrderBy[] orderBy, @Nullable Limit limit) {

        if (joins != null && joins.length > 0) {
            try {
                String joinQuery = mClauseHelper.buildJoinQuery(
                        columns,
                        joins,
                        tableName,
                        where,
                        orderBy,
                        limit,
                        getResolver()
                );

                return mDatabase.rawQuery(joinQuery, mClauseHelper.getConditionArgs(where));
            } catch (Exception e) {
                throw new SQLException(e.getMessage());
            }
        } else {
            Cursor cursor = mDatabase.query(
                    tableName,
                    columns,
                    mClauseHelper.getCondition(where),
                    mClauseHelper.getConditionArgs(where),
                    groupBy,
                    having,
                    mClauseHelper.getOrderBy(orderBy),
                    mClauseHelper.getLimit(limit)
            );

            if (cursor != null) {
                cursor.setNotificationUri(mContext.getContentResolver(), getUri(tableName, null));
            }

            return cursor;
        }
    }

    protected int delete(@NonNull String tableName, @Nullable String uriSuffix, @Nullable Where[] where) {
        String whereClause = mClauseHelper.getCondition(where);
        int nbDeleted = mDatabase.delete(
                tableName,
                whereClause,
                mClauseHelper.getConditionArgs(where)
        );

        if (nbDeleted > 0 || TextUtils.isEmpty(whereClause)) {
            mContext.getContentResolver().notifyChange(getUri(tableName, uriSuffix), null);
        }

        return nbDeleted;
    }

    protected long count(@NonNull String tableName, @Nullable Where[] where) {
        return DatabaseUtils.queryNumEntries(
                mDatabase,
                tableName,
                mClauseHelper.getCondition(where),
                mClauseHelper.getConditionArgs(where)
        );
    }

    protected
    @Nullable
    Cursor rawQuery(@NonNull String sql) {
        return mDatabase.rawQuery(sql, null);
    }

    @NonNull
    SQLiteDatabase getDatabase() {
        return mDatabase;
    }
}