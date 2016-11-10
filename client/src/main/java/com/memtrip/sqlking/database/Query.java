package com.memtrip.sqlking.database;

import android.content.ContentValues;
import android.database.Cursor;

import com.memtrip.sqlking.common.SQLQuery;
import com.memtrip.sqlking.operation.function.Count;
import com.memtrip.sqlking.operation.function.Delete;
import com.memtrip.sqlking.operation.function.Insert;
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
            SQLQuery sqlQuery = getSQLQuery(classDef, databaseProvider);
            for (int i = 0; i < models.length; i++) {
                valuesArray[i] = sqlQuery.getContentValues(models[i]);
            }
            databaseProvider.bulkInsert(sqlQuery.getTableName(), valuesArray);
        }
    }

    protected static Cursor selectCursor(Select select, Class<?> classDef, DatabaseProvider databaseProvider) {

        SQLQuery sqlQuery = getSQLQuery(classDef, databaseProvider);

        return databaseProvider.query(
                sqlQuery.getTableName(),
                sqlQuery.getColumnNames(),
                select.getClause(),
                select.getJoin(),
                null,
                null,
                select.getOrderBy(),
                select.getLimit()
        );
    }

    protected static <T> T[] select(Select select, Class<?> classDef, DatabaseProvider databaseProvider) {
        Cursor cursor = selectCursor(select, classDef, databaseProvider);
        return getSQLQuery(classDef, databaseProvider).retrieveSQLSelectResults(cursor);
    }

    protected static <T> T selectSingle(Select select, Class<?> classDef, DatabaseProvider databaseProvider) {
        Cursor cursor = selectCursor(select, classDef, databaseProvider);

        T[] results = getSQLQuery(classDef, databaseProvider).retrieveSQLSelectResults(cursor);

        if (results != null && results.length > 0) {
            return results[0];
        } else {
            return null;
        }
    }

    protected static int update(Update update, Class<?> classDef, DatabaseProvider databaseProvider) {
        return databaseProvider.update(
                getSQLQuery(classDef, databaseProvider).getTableName(),
                update.getContentValues(),
                update.getConditions()
        );
    }

    protected static long count(Count count, Class<?> classDef, DatabaseProvider databaseProvider) {
        return databaseProvider.count(
                getSQLQuery(classDef, databaseProvider).getTableName(),
                count.getClause()
        );
    }

    protected static int delete(Delete delete, Class<?> classDef, DatabaseProvider databaseProvider) {
        return databaseProvider.delete(
                getSQLQuery(classDef, databaseProvider).getTableName(),
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

    private static SQLQuery getSQLQuery(Class<?> classDef, DatabaseProvider databaseProvider) {
        return databaseProvider.getResolver().getSQLQuery(classDef);
    }
}
