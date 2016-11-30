package net.frju.androidquery.operation.function;

import android.database.Cursor;
import android.database.CursorWrapper;

import net.frju.androidquery.database.Resolver;
import net.frju.androidquery.database.TableDescription;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Result<T> extends CursorWrapper implements Iterable<T> {

    public class ResultIterator implements Iterator<T> {

        public boolean hasNext() {
            return getCount() > 0 && getPosition() < getCount() - 1;
        }

        public T next() {
            moveToNext();
            return get();
        }

        public void remove() {
            throw new UnsupportedOperationException("Cannot remove item from Result");
        }
    }

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
        return new ArrayList<>(Arrays.asList(toArray())); // need to copy the array otherwise it's not modifiable
    }

    @Override
    public Iterator<T> iterator() {
        return new ResultIterator();
    }
}
