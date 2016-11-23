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
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import net.frju.androidquery.operation.clause.Clause;
import net.frju.androidquery.operation.join.Join;
import net.frju.androidquery.operation.keyword.Limit;
import net.frju.androidquery.operation.keyword.OrderBy;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Samuel Kirton [sam@memtrip.com]
 */
public abstract class BaseLocalDatabaseProvider extends DatabaseProvider {

    private SQLiteDatabase mDatabase;
    private String[] mSchemaArray;
    private String[] mTableRealNameArray;
    private String[] mCreateIndexQuery;
    private List<String> mIndexNames;

    public BaseLocalDatabaseProvider(Context context) {

        Class<?> modelClassDef[] = getResolver().getModelsForProvider(this.getClass());
        int modelCount = modelClassDef.length;

        mSchemaArray = new String[modelCount];
        mTableRealNameArray = new String[modelCount];
        mCreateIndexQuery = new String[modelCount];
        mIndexNames = new ArrayList<>();

        for (int i = 0; i < modelClassDef.length; i++) {
            TableDescription tableDescription = getResolver().getTableDescription(modelClassDef[i]);
            mSchemaArray[i] = tableDescription.getTableInsertQuery();
            mTableRealNameArray[i] = tableDescription.getTableRealName();
            mCreateIndexQuery[i] = tableDescription.getCreateIndexQuery();

            for (String indexName : tableDescription.getIndexNames()) {
                mIndexNames.add(indexName);
            }
        }

        SQLiteOpenHelper openHelper = new SQLiteOpenHelper(context, getDbName(), null, getDbVersion()) {
            @Override
            public void onCreate(SQLiteDatabase db) {
                BaseLocalDatabaseProvider.this.onCreate(db);
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                BaseLocalDatabaseProvider.this.onUpgrade(db, oldVersion, newVersion);
            }
        };

        mDatabase = openHelper.getWritableDatabase();
    }

    protected abstract String getDbName();

    protected abstract int getDbVersion();

    protected void onCreate(SQLiteDatabase db) {
        for (String schema : mSchemaArray) {
            db.execSQL(schema);
        }

        for (String createIndex : mCreateIndexQuery) {
            if (createIndex != null) {
                db.execSQL(createIndex);
            }
        }
    }

    protected void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            //TODO: for now it destroy everything...
            for (String tableName : mTableRealNameArray) {
                db.execSQL("DROP TABLE IF EXISTS " + tableName);
            }

            for (String index : mIndexNames) {
                db.execSQL("DROP INDEX IF EXISTS " + index);
            }
            onCreate(db);
        }
    }

    protected int bulkInsert(String tableName, ContentValues[] valuesArray) {
        int nbInsert = 0;
        mDatabase.beginTransaction();

        for (ContentValues values : valuesArray) {
            if (mDatabase.insert(tableName, null, values) != -1) {
                nbInsert++;
            }
        }

        mDatabase.setTransactionSuccessful();
        mDatabase.endTransaction();

        return nbInsert;
    }

    protected int update(String tableName, ContentValues values, Clause[] clause) {
        return mDatabase.update(
                tableName,
                values,
                mClauseHelper.getClause(clause),
                mClauseHelper.getClauseArgs(clause)
        );
    }

    protected Cursor query(String tableName, String[] columns, Clause[] clause, Join[] joins,
                           String groupBy, String having, OrderBy[] orderBy, Limit limit) {

        if (joins != null && joins.length > 0) {
            try {
                String joinQuery = mClauseHelper.buildJoinQuery(
                        columns,
                        joins,
                        tableName,
                        clause,
                        orderBy,
                        limit,
                        getResolver()
                );

                return mDatabase.rawQuery(joinQuery, mClauseHelper.getClauseArgs(clause));
            } catch (Exception e) {
                throw new SQLException(e.getMessage());
            }
        } else {
            return mDatabase.query(
                    tableName,
                    columns,
                    mClauseHelper.getClause(clause),
                    mClauseHelper.getClauseArgs(clause),
                    groupBy,
                    having,
                    mClauseHelper.getOrderBy(orderBy),
                    mClauseHelper.getLimit(limit)
            );
        }
    }

    protected int delete(String tableName, Clause[] clause) {
        return mDatabase.delete(
                tableName,
                mClauseHelper.getClause(clause),
                mClauseHelper.getClauseArgs(clause)
        );
    }

    protected long count(String tableName, Clause[] clause) {
        return DatabaseUtils.queryNumEntries(
                mDatabase,
                tableName,
                mClauseHelper.getClause(clause),
                mClauseHelper.getClauseArgs(clause)
        );
    }

    protected Cursor rawQuery(String sql) {
        return mDatabase.rawQuery(sql, null);
    }

    SQLiteDatabase getDatabase() {
        return mDatabase;
    }
}