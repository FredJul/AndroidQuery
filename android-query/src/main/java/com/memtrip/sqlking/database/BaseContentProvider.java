package com.memtrip.sqlking.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;

import java.util.List;

public abstract class BaseContentProvider extends ContentProvider {

    private SQLiteDatabase mDatabase;

    @Override
    public String getType(Uri uri) {
        try {
            Long.valueOf(uri.getLastPathSegment());
            return "vnd.android.cursor.item/model";
        } catch (NumberFormatException e) { // if last segment not an id, it should be the table name
            return "vnd.android.cursor.dir/model";
        }
    }

    @Override
    public boolean onCreate() {
        mDatabase = getLocalSQLProvider().getDatabase();
        return true;
    }

    protected abstract BaseLocalDatabaseProvider getLocalSQLProvider();

    protected static String[] getTableRealNameAndSelection(Uri uri, String selection) {
        String tableName;
        String lastSegment = uri.getLastPathSegment();
        try {
            long id = Long.valueOf(lastSegment);
            List<String> segments = uri.getPathSegments();
            tableName = BaseContentDatabaseProvider.firstToUpperCase(segments.get(segments.size() - 2));
            String idSelection = BaseColumns._ID + "=" + id;

            if (TextUtils.isEmpty(selection)) {
                selection = idSelection;
            } else {
                selection = idSelection + " AND (" + selection + ")";
            }
        } catch (NumberFormatException e) { // if last segment not an id, it should be the table name
            tableName = BaseContentDatabaseProvider.firstToUpperCase(uri.getLastPathSegment());
        }

        return new String[]{tableName, selection};
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        String[] nameAndSelection = getTableRealNameAndSelection(uri, selection);

        Cursor cursor = mDatabase.query(nameAndSelection[0], projection, nameAndSelection[1], selectionArgs, null, null,
                sortOrder, null);

        if (cursor != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return cursor;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] valuesArray) {
        int nbInsert = 0;

        mDatabase.beginTransaction();
        for (ContentValues values : valuesArray) {
            long newId = mDatabase.insert(uri.getLastPathSegment(), null, values);

            if (newId > -1) {
                nbInsert++;
            }
        }
        mDatabase.setTransactionSuccessful();
        mDatabase.endTransaction();

        getContext().getContentResolver().notifyChange(uri, null);

        return nbInsert;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long newId = mDatabase.insert(uri.getLastPathSegment(), null, values);

        if (newId > -1) {
            getContext().getContentResolver().notifyChange(uri, null);
            return ContentUris.withAppendedId(uri, newId);
        } else {
            throw new SQLException("Could not insert row into " + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        String[] nameAndSelection = getTableRealNameAndSelection(uri, selection);

        int count = mDatabase.update(nameAndSelection[0], values, nameAndSelection[1], selectionArgs);
        if (count > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return count;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        String[] nameAndSelection = getTableRealNameAndSelection(uri, selection);

        int count = mDatabase.delete(nameAndSelection[0], nameAndSelection[1], selectionArgs);
        if (count > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return count;
    }
}