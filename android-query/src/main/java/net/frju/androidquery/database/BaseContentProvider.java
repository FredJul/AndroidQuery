package net.frju.androidquery.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.List;

public abstract class BaseContentProvider extends ContentProvider {

    private SQLiteDatabase mDatabase;

    @Override
    public String getType(@NonNull Uri uri) {
        try {
            //noinspection ResultOfMethodCallIgnored
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

    protected abstract
    @NonNull
    BaseLocalDatabaseProvider getLocalSQLProvider();

    protected static
    @NonNull
    String[] getTableRealNameAndSelection(@NonNull Uri uri, @Nullable String selection) {
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
    public
    @Nullable
    Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        String[] nameAndSelection = getTableRealNameAndSelection(uri, selection);

        Cursor cursor = mDatabase.query(nameAndSelection[0], projection, nameAndSelection[1], selectionArgs, null, null,
                sortOrder, null);

        if (cursor != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return cursor;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] valuesArray) {
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
    public
    @NonNull
    Uri insert(@NonNull Uri uri, @NonNull ContentValues values) {
        long newId = mDatabase.insert(uri.getLastPathSegment(), null, values);

        if (newId > -1) {
            getContext().getContentResolver().notifyChange(uri, null);
            return ContentUris.withAppendedId(uri, newId);
        } else {
            throw new SQLException("Could not insert row into " + uri);
        }
    }

    @Override
    public int update(@NonNull Uri uri, @NonNull ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        String[] nameAndSelection = getTableRealNameAndSelection(uri, selection);

        int count = mDatabase.update(nameAndSelection[0], values, nameAndSelection[1], selectionArgs);
        if (count > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return count;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        String[] nameAndSelection = getTableRealNameAndSelection(uri, selection);

        int nbDeleted = mDatabase.delete(nameAndSelection[0], nameAndSelection[1], selectionArgs);
        if (nbDeleted > 0 || TextUtils.isEmpty(nameAndSelection[1])) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return nbDeleted;
    }
}