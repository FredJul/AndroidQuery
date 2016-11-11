package com.memtrip.sqlking.operation.function;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.memtrip.sqlking.database.Resolver;
import com.memtrip.sqlking.database.TableDescription;

import java.util.Arrays;
import java.util.List;

public class Result<T> extends CursorWrapper {

    private TableDescription mQuery;

    public Result(Class<T> type, Resolver resolver, Cursor cursor) {
        super(cursor);
        mQuery = resolver.getTableDescription(type);
    }

    public T get() {
        return mQuery.getSingleResult(this);
    }

    public T get(int position) {
        moveToPosition(position);
        return mQuery.getSingleResult(this);
    }

    public T[] asArray() {
        return mQuery.getArrayResult(this);
    }

    public List<T> asList() {
        return Arrays.asList(asArray());
    }
}
