package com.memtrip.sqlking.database;

import android.content.ContentValues;
import android.database.Cursor;

import com.memtrip.sqlking.operation.function.Count;
import com.memtrip.sqlking.operation.function.Delete;
import com.memtrip.sqlking.operation.function.Insert;
import com.memtrip.sqlking.operation.function.Result;
import com.memtrip.sqlking.operation.function.Select;
import com.memtrip.sqlking.operation.function.Update;

import java.util.concurrent.Callable;

import rx.Observable;
import rx.Subscriber;

public abstract class Query {

    protected static void insert(Insert insert, Class<?> classDef, DatabaseProvider databaseProvider) {
        if (insert.getModels() != null && insert.getModels().length > 0) {
            Object[] models = insert.getModels();
            ContentValues[] valuesArray = new ContentValues[models.length];
            TableDescription tableDescription = getTableDescription(classDef, databaseProvider);
            for (int i = 0; i < models.length; i++) {
                valuesArray[i] = tableDescription.getContentValues(models[i]);
            }
            databaseProvider.bulkInsert(tableDescription.getTableRealName(), valuesArray);
        }
    }

    protected static Cursor selectCursor(Select select, Class<?> classDef, DatabaseProvider databaseProvider) {

        TableDescription tableDescription = getTableDescription(classDef, databaseProvider);

        return databaseProvider.query(
                tableDescription.getTableRealName(),
                select.getJoin() != null ? tableDescription.getColumnNamesWithTablePrefix() : tableDescription.getColumnNames(),
                select.getClause(),
                select.getJoin(),
                null,
                null,
                select.getOrderBy(),
                select.getLimit()
        );
    }

    protected static <T> Result<T> select(Select select, Class<T> classDef, DatabaseProvider databaseProvider) {
        Cursor cursor = selectCursor(select, classDef, databaseProvider);
        return new Result<>(classDef, databaseProvider.getResolver(), cursor);
    }

    protected static <T> T selectSingle(Select select, Class<T> classDef, DatabaseProvider databaseProvider) {
        Cursor cursor = selectCursor(select, classDef, databaseProvider);

        T[] results = getTableDescription(classDef, databaseProvider).getArrayResult(cursor);

        if (results != null && results.length > 0) {
            return results[0];
        } else {
            return null;
        }
    }

    protected static int update(Update update, Class<?> classDef, DatabaseProvider databaseProvider) {
        return databaseProvider.update(
                getTableDescription(classDef, databaseProvider).getTableRealName(),
                update.getContentValues(),
                update.getConditions()
        );
    }

    protected static long count(Count count, Class<?> classDef, DatabaseProvider databaseProvider) {
        return databaseProvider.count(
                getTableDescription(classDef, databaseProvider).getTableRealName(),
                count.getClause()
        );
    }

    protected static int delete(Delete delete, Class<?> classDef, DatabaseProvider databaseProvider) {
        return databaseProvider.delete(
                getTableDescription(classDef, databaseProvider).getTableRealName(),
                delete.getConditions()
        );
    }

    protected static Cursor rawQuery(String query, DatabaseProvider databaseProvider) {
        return databaseProvider.rawQuery(query);
    }

    protected static <T> Observable<T> wrapRx(final Callable<T> func) {
        return Observable.create(
                new Observable.OnSubscribe<T>() {
                    @Override
                    public void call(Subscriber<? super T> subscriber) {
                        try {
                            subscriber.onNext(func.call());
                        } catch (Exception e) {
                            subscriber.onError(e);
                        }
                    }
                }
        );
    }

    private static TableDescription getTableDescription(Class<?> classDef, DatabaseProvider databaseProvider) {
        return databaseProvider.getResolver().getTableDescription(classDef);
    }
}
