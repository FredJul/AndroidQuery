package net.frju.androidquery.database;

import android.content.ContentValues;
import android.database.Cursor;

import net.frju.androidquery.operation.clause.Clause;
import net.frju.androidquery.operation.clause.In;
import net.frju.androidquery.operation.clause.Where;
import net.frju.androidquery.operation.function.Count;
import net.frju.androidquery.operation.function.Delete;
import net.frju.androidquery.operation.function.Insert;
import net.frju.androidquery.operation.function.Result;
import net.frju.androidquery.operation.function.Select;
import net.frju.androidquery.operation.function.Update;

import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

public abstract class Query {

    protected static int insert(Insert insert, Class<?> classDef, DatabaseProvider databaseProvider) {
        if (insert.getModels() != null && insert.getModels().length > 0) {
            Object[] models = insert.getModels();
            ContentValues[] valuesArray = new ContentValues[models.length];
            TableDescription tableDescription = getTableDescription(classDef, databaseProvider);
            for (int i = 0; i < models.length; i++) {
                if (models[i] instanceof ModelListener) {
                    ((ModelListener) models[i]).onPreInsert();
                }
                valuesArray[i] = tableDescription.getContentValues(models[i]);
            }
            return databaseProvider.bulkInsert(tableDescription.getTableRealName(), valuesArray);
        }

        return 0;
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
        if (update.getModel() != null) {
            TableDescription tableDesc = getTableDescription(classDef, databaseProvider);
            Clause[] conditions = update.getConditions();
            ContentValues values = tableDesc.getContentValues(update.getModel());
            if (conditions == null) {
                conditions = new Clause[1];
                conditions[0] = Where.where(tableDesc.getPrimaryKeyRealName(), Where.Exp.EQUAL_TO, values.get(tableDesc.getPrimaryKeyRealName()));
            }

            if (update.getModel() instanceof ModelListener) {
                ((ModelListener) update.getModel()).onPreUpdate();
            }

            return databaseProvider.update(
                    tableDesc.getTableRealName(),
                    values,
                    conditions
            );
        } else {
            return databaseProvider.update(
                    getTableDescription(classDef, databaseProvider).getTableRealName(),
                    update.getContentValues(),
                    update.getConditions()
            );
        }
    }

    protected static long count(Count count, Class<?> classDef, DatabaseProvider databaseProvider) {
        return databaseProvider.count(
                getTableDescription(classDef, databaseProvider).getTableRealName(),
                count.getClause()
        );
    }

    protected static int delete(Delete delete, Class<?> classDef, DatabaseProvider databaseProvider) {
        Object[] models = delete.getModels();

        if (models != null) {
            TableDescription tableDesc = getTableDescription(classDef, databaseProvider);

            Object[] keys = new String[delete.getModels().length];
            for (int i = 0; i < models.length; i++) {
                keys[i] = tableDesc.getPrimaryKeyValue(models[i]);

                if (models[i] instanceof ModelListener) {
                    ((ModelListener) models[i]).onPreDelete();
                }
            }

            Clause condition = new In(tableDesc.getPrimaryKeyRealName(), keys);

            return databaseProvider.delete(
                    tableDesc.getTableRealName(),
                    new Clause[]{condition}
            );
        } else {
            return databaseProvider.delete(
                    getTableDescription(classDef, databaseProvider).getTableRealName(),
                    delete.getConditions()
            );
        }
    }

    protected static Cursor rawQuery(String query, DatabaseProvider databaseProvider) {
        return databaseProvider.rawQuery(query);
    }

    protected static <T> Observable<T> wrapRx(final Callable<T> func) {
        return Observable.create(
                new ObservableOnSubscribe<T>() {
                    @Override
                    public void subscribe(ObservableEmitter<T> emitter) throws Exception {
                        try {
                            emitter.onNext(func.call());
                        } catch (Exception e) {
                            emitter.onError(e);
                        }
                    }
                }
        );
    }

    private static TableDescription getTableDescription(Class<?> classDef, DatabaseProvider databaseProvider) {
        return databaseProvider.getResolver().getTableDescription(classDef);
    }
}
