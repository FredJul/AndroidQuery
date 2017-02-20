package net.frju.androidquery.operation.function;

import android.database.Cursor;
import android.database.CursorWrapper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import net.frju.androidquery.database.DbModelDescriptor;
import net.frju.androidquery.database.Resolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class CursorResult<T> extends CursorWrapper implements Iterable<T> {

    public class ResultIterator implements Iterator<T> {

        public boolean hasNext() {
            return getCount() > 0 && getPosition() < getCount() - 1;
        }

        public
        @Nullable
        T next() {
            moveToNext();
            return get();
        }

        public void remove() {
            throw new UnsupportedOperationException("Cannot remove item from CursorResult");
        }
    }

    private final DbModelDescriptor mQuery;

    public CursorResult(@NonNull Class<T> type, @NonNull Resolver resolver, Cursor cursor) {
        super(cursor);
        mQuery = resolver.getDbModelDescriptor(type);
    }

    public
    @Nullable
    T get() {
        return mQuery.getSingleResult(this);
    }

    public
    @Nullable
    T get(int position) {
        moveToPosition(position);
        return mQuery.getSingleResult(this);
    }

    public
    @NonNull
    T[] toArray() {
        return mQuery.getArrayResult(this);
    }

    public
    @NonNull
    List<T> toList() {
        if (getWrappedCursor() != null) {
            return new ArrayList<>(Arrays.asList(toArray())); // need to copy the array otherwise it's not modifiable
        }

        return null;
    }

    @Override
    public int getCount() {
        if (getWrappedCursor() != null) {
            return super.getCount();
        }
        return 0;
    }

    @Override
    public
    @NonNull
    Iterator<T> iterator() {
        return new ResultIterator();
    }
}
