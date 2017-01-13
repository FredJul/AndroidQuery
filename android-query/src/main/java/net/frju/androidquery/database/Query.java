package net.frju.androidquery.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

import net.frju.androidquery.operation.condition.Where;
import net.frju.androidquery.operation.function.Count;
import net.frju.androidquery.operation.function.CursorResult;
import net.frju.androidquery.operation.function.Delete;
import net.frju.androidquery.operation.function.Insert;
import net.frju.androidquery.operation.function.Save;
import net.frju.androidquery.operation.function.Select;
import net.frju.androidquery.operation.function.Update;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import rx.SingleSubscriber;

public abstract class Query {

    protected static int save(Save save, Class<?> classDef, DatabaseProvider databaseProvider) {
        int nb = 0;

        DbModelDescriptor table = databaseProvider.getResolver().getDbModelDescriptor(classDef);
        boolean isPrimaryKeyAutoIncrement = table.isPrimaryKeyAutoIncrement();

        ArrayList<Object> modelsToInsert = new ArrayList<>();
        for (Object model : save.getModels()) {
            long id = 1; // first valid autoincrement id isEqualTo always >= 1
            if (isPrimaryKeyAutoIncrement) {
                // Try to guess if we for sure need to insert thanks to primary key
                Object primaryKeyValue = table.getPrimaryKeyValue(model);
                id = Long.parseLong(primaryKeyValue.toString()); // should be a short, int or long
            }

            //noinspection unchecked
            if (id <= 0 || Update.getBuilder(classDef, databaseProvider).model(model).query() <= 0) {
                modelsToInsert.add(model);
            } else {
                nb++;
            }
        }

        nb += Insert.getBuilder(databaseProvider, modelsToInsert.toArray()).query();

        return nb;
    }

    protected static int insert(Insert insert, Class<?> classDef, DatabaseProvider databaseProvider) {
        if (insert.getModels() != null && insert.getModels().length > 0) {
            Object[] models = insert.getModels();
            ContentValues[] valuesArray = new ContentValues[models.length];
            DbModelDescriptor dbModelDescriptor = getTableDescription(classDef, databaseProvider);
            for (int i = 0; i < models.length; i++) {
                if (models[i] instanceof ModelListener) {
                    ((ModelListener) models[i]).onPreInsert();
                }
                valuesArray[i] = dbModelDescriptor.getContentValues(models[i]);
            }

            if (models.length == 1) {
                long newId = databaseProvider.insert(dbModelDescriptor.getTableDbName(), valuesArray[0]);
                if (newId != -1) {
                    dbModelDescriptor.setIdToModel(models[0], newId);
                    return 1;
                } else {
                    return 0;
                }
            } else {
                return databaseProvider.bulkInsert(dbModelDescriptor.getTableDbName(), valuesArray);
            }
        }

        return 0;
    }

    protected static Cursor selectCursor(Select select, Class<?> classDef, DatabaseProvider databaseProvider) {

        DbModelDescriptor dbModelDescriptor = getTableDescription(classDef, databaseProvider);

        return databaseProvider.query(
                dbModelDescriptor.getTableDbName(),
                select.getJoins() != null ? dbModelDescriptor.getColumnNamesWithTablePrefix() : dbModelDescriptor.getColumnNames(),
                select.getClause(),
                select.getJoins(),
                null,
                null,
                select.getOrderBy(),
                select.getLimit()
        );
    }

    protected static <T> CursorResult<T> select(Select select, Class<T> classDef, DatabaseProvider databaseProvider) {
        Cursor cursor = selectCursor(select, classDef, databaseProvider);
        return new CursorResult<>(classDef, databaseProvider.getResolver(), cursor);
    }

    protected static <T> T[] selectAndInit(Select select, Class<T> classDef, DatabaseProvider databaseProvider) {
        Cursor cursor = selectCursor(select, classDef, databaseProvider);

        Resolver resolver = databaseProvider.getResolver();
        T[] result = new CursorResult<>(classDef, resolver, cursor).toArray();

        for (T object : result) {
            resolver.initModelWithInitMethods(object);
        }

        return result;
    }

    protected static <T> T selectFirst(Select select, Class<T> classDef, DatabaseProvider databaseProvider) {
        Cursor cursor = selectCursor(select, classDef, databaseProvider);

        T[] results = getTableDescription(classDef, databaseProvider).getArrayResult(cursor);

        if (results != null && results.length > 0) {
            return results[0];
        } else {
            return null;
        }
    }

    protected static <T> T selectFirstAndInit(Select select, Class<T> classDef, DatabaseProvider databaseProvider) {
        Cursor cursor = selectCursor(select, classDef, databaseProvider);

        T[] results = getTableDescription(classDef, databaseProvider).getArrayResult(cursor);

        if (results != null && results.length > 0) {
            databaseProvider.getResolver().initModelWithInitMethods(results[0]);
            return results[0];
        } else {
            return null;
        }
    }

    protected static int update(Update update, Class<?> classDef, DatabaseProvider databaseProvider) {
        Object[] models = update.getModels();
        if (models != null) {
            DbModelDescriptor tableDesc = getTableDescription(classDef, databaseProvider);
            String primaryKeyName = tableDesc.getPrimaryKeyDbName();
            ContentValues[] valuesArray = new ContentValues[models.length];
            Where[][] conditionsArray = new Where[models.length][];
            for (int i = 0; i < models.length; i++) {
                Object model = models[i];

                conditionsArray[i] = update.getConditions();
                if (conditionsArray[i] == null) {
                    if (TextUtils.isEmpty(primaryKeyName)) {
                        throw new IllegalStateException("update with model() method require a primary key");
                    }
                    conditionsArray[i] = new Where[1];
                    conditionsArray[i][0] = Where.field(primaryKeyName).isEqualTo(tableDesc.getPrimaryKeyValue(model));
                }

                if (model instanceof ModelListener) {
                    ((ModelListener) model).onPreUpdate();
                }

                valuesArray[i] = tableDesc.getContentValues(model);
            }
            return databaseProvider.bulkUpdate(
                    tableDesc.getTableDbName(),
                    valuesArray,
                    conditionsArray
            );
        } else {
            return databaseProvider.bulkUpdate(
                    getTableDescription(classDef, databaseProvider).getTableDbName(),
                    new ContentValues[]{update.getContentValues()},
                    new Where[][]{update.getConditions()}
            );
        }
    }

    protected static long count(Count count, Class<?> classDef, DatabaseProvider databaseProvider) {
        return databaseProvider.count(
                getTableDescription(classDef, databaseProvider).getTableDbName(),
                count.getClause()
        );
    }

    protected static int delete(Delete delete, Class<?> classDef, DatabaseProvider databaseProvider) {
        Object[] models = delete.getModels();

        if (models != null) {
            DbModelDescriptor tableDesc = getTableDescription(classDef, databaseProvider);
            String primaryKeyName = tableDesc.getPrimaryKeyDbName();
            if (TextUtils.isEmpty(primaryKeyName)) {
                throw new IllegalStateException("delete with model() method require a primary key");
            }

            Object[] keys = new String[delete.getModels().length];
            for (int i = 0; i < models.length; i++) {
                keys[i] = tableDesc.getPrimaryKeyValue(models[i]);

                if (models[i] instanceof ModelListener) {
                    ((ModelListener) models[i]).onPreDelete();
                }
            }

            Where where = Where.field(primaryKeyName).isIn(keys);

            return databaseProvider.delete(
                    tableDesc.getTableDbName(),
                    new Where[]{where}
            );
        } else {
            return databaseProvider.delete(
                    getTableDescription(classDef, databaseProvider).getTableDbName(),
                    delete.getConditions()
            );
        }
    }

    protected static Cursor rawQuery(String query, DatabaseProvider databaseProvider) {
        return databaseProvider.rawQuery(query);
    }

    protected static <T> rx.Single<T> wrapRx(final Callable<T> func) {
        return rx.Single.create(
                new rx.Single.OnSubscribe<T>() {
                    @Override
                    public void call(SingleSubscriber<? super T> singleSubscriber) {
                        try {
                            singleSubscriber.onSuccess(func.call());
                        } catch (Exception e) {
                            singleSubscriber.onError(e);
                        }
                    }
                }
        ).subscribeOn(rx.schedulers.Schedulers.io())
                .observeOn(rx.android.schedulers.AndroidSchedulers.mainThread());
    }

    protected static <T> Single<T> wrapRx2(final Callable<T> func) {
        return Single.create(
                new SingleOnSubscribe<T>() {
                    @Override
                    public void subscribe(SingleEmitter<T> emitter) throws Exception {
                        try {
                            emitter.onSuccess(func.call());
                        } catch (Exception e) {
                            emitter.onError(e);
                        }
                    }
                }
        ).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private static DbModelDescriptor getTableDescription(Class<?> classDef, DatabaseProvider databaseProvider) {
        return databaseProvider.getResolver().getDbModelDescriptor(classDef);
    }
}
