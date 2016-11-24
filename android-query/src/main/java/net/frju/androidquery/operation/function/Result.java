package net.frju.androidquery.operation.function;

import android.database.Cursor;
import android.database.CursorWrapper;

import net.frju.androidquery.database.Resolver;
import net.frju.androidquery.database.TableDescription;

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

    public T[] toArray() {
        return mQuery.getArrayResult(this);
    }

    public List<T> toList() {
        return Arrays.asList(toArray());
    }
}
