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
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.memtrip.sqlking.common.Resolver;
import com.memtrip.sqlking.common.SQLQuery;
import com.memtrip.sqlking.operation.clause.Clause;
import com.memtrip.sqlking.operation.join.Join;
import com.memtrip.sqlking.operation.keyword.Limit;
import com.memtrip.sqlking.operation.keyword.OrderBy;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Samuel Kirton [sam@memtrip.com]
 */
public class LocalDatabaseProvider extends DatabaseProvider {

    private SQLiteDatabase mDatabase;
    private String[] mSchemaArray;
    private String[] mTableNameArray;
    private String[] mCreateIndexQuery;
    private List<String> mIndexNames;

    public LocalDatabaseProvider(Context context,
                                 String name,
                                 int version,
                                 Resolver resolver,
                                 Class<?>... modelClassDef) {
        super(resolver);

        int modelCount = modelClassDef.length;

        mSchemaArray = new String[modelCount];
        mTableNameArray = new String[modelCount];
        mCreateIndexQuery = new String[modelCount];
        mIndexNames = new ArrayList<>();

        for (int i = 0; i < modelClassDef.length; i++) {
            SQLQuery sqlQuery = resolver.getSQLQuery(modelClassDef[i]);
            mSchemaArray[i] = sqlQuery.getTableInsertQuery();
            mTableNameArray[i] = sqlQuery.getTableName();
            mCreateIndexQuery[i] = sqlQuery.getCreateIndexQuery();

            for (String indexName : sqlQuery.getIndexNames()) {
                mIndexNames.add(indexName);
            }
        }

        SQLiteOpenHelper openHelper = new SQLiteOpenHelper(context, name, null, version) {
            @Override
            public void onCreate(SQLiteDatabase db) {
                LocalDatabaseProvider.this.onCreate(db);
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                LocalDatabaseProvider.this.onUpgrade(db, oldVersion, newVersion);
            }
        };

        mDatabase = openHelper.getWritableDatabase();
    }

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
            for (String tableName : mTableNameArray) {
                db.execSQL("DROP TABLE IF EXISTS " + tableName);
            }

            for (String index : mIndexNames) {
                db.execSQL("DROP INDEX IF EXISTS " + index);
            }
            onCreate(db);
        }
    }

    protected void bulkInsert(String tableName, ContentValues[] valuesArray) {
        mDatabase.beginTransaction();

        for (ContentValues values : valuesArray) {
            mDatabase.insert(tableName, null, values);
        }

        mDatabase.setTransactionSuccessful();
        mDatabase.endTransaction();
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
                           String groupBy, String having, OrderBy orderBy, Limit limit) {

        if (joins != null && joins.length > 0) {
            try {
                String joinQuery = mClauseHelper.buildJoinQuery(
                        columns,
                        joins,
                        tableName,
                        clause,
                        orderBy,
                        limit,
                        mResolver
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