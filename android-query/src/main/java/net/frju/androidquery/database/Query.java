package net.frju.androidquery.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import net.frju.androidquery.operation.condition.Where;
import net.frju.androidquery.operation.function.Count;
import net.frju.androidquery.operation.function.CursorResult;
import net.frju.androidquery.operation.function.Delete;
import net.frju.androidquery.operation.function.Insert;
import net.frju.androidquery.operation.function.Save;
import net.frju.androidquery.operation.function.Select;
import net.frju.androidquery.operation.function.Update;
import net.frju.androidquery.operation.keyword.Limit;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import rx.SingleSubscriber;

public abstract class Query {

    public enum ConflictResolution {
        /**
         * When a constraint violation occurs, an immediate ROLLBACK occurs,
         * thus ending the current transaction, and the command aborts with a
         * return code of SQLITE_CONSTRAINT. If no transaction is active
         * (other than the implied transaction that is created on every command)
         * then this algorithm works the same as ABORT.
         */
        CONFLICT_ROLLBACK,

        /**
         * When a constraint violation occurs,no ROLLBACK is executed
         * so changes from prior commands within the same transaction
         * are preserved. This is the default behavior.
         */
        CONFLICT_ABORT,

        /**
         * When a constraint violation occurs, the command aborts with a return
         * code SQLITE_CONSTRAINT. But any changes to the database that
         * the command made prior to encountering the constraint violation
         * are preserved and are not backed out.
         */
        CONFLICT_FAIL,

        /**
         * When a constraint violation occurs, the one row that contains
         * the constraint violation is not inserted or changed.
         * But the command continues executing normally. Other rows before and
         * after the row that contained the constraint violation continue to be
         * inserted or updated normally. No error is returned.
         */
        CONFLICT_IGNORE,

        /**
         * When a UNIQUE constraint violation occurs, the pre-existing rows that
         * are causing the constraint violation are removed prior to inserting
         * or updating the current row. Thus the insert or update always occurs.
         * The command continues executing normally. No error is returned.
         * If a NOT NULL constraint violation occurs, the NULL value is replaced
         * by the default value for that column. If the column has no default
         * value, then the ABORT algorithm is used. If a CHECK constraint
         * violation occurs then the IGNORE algorithm is used. When this conflict
         * resolution strategy deletes rows in order to satisfy a constraint,
         * it does not invoke delete triggers on those rows.
         * This behavior might change in a future release.
         */
        CONFLICT_REPLACE
    }

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
                if (primaryKeyValue != null) {
                    id = Long.parseLong(primaryKeyValue.toString()); // should be a short, int or long
                }
            }

            //noinspection unchecked
            if (id <= 0 || Update.getBuilder(classDef, databaseProvider)
                    .withConflictResolution(save.getConflictResolution())
                    .model(model)
                    .query() <= 0) {
                modelsToInsert.add(model);
            } else {
                nb++;
            }
        }

        nb += Insert.getBuilder(databaseProvider, modelsToInsert.toArray())
                .withConflictResolution(save.getConflictResolution())
                .query();

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
                long newId = databaseProvider.insert(dbModelDescriptor.getTableDbName(), valuesArray[0], insert.getConflictResolution());
                if (newId != -1) {
                    dbModelDescriptor.setIdToModel(models[0], newId);
                    return 1;
                } else {
                    return 0;
                }
            } else {
                return databaseProvider.bulkInsert(dbModelDescriptor.getTableDbName(), valuesArray, insert.getConflictResolution());
            }
        }

        return 0;
    }

    protected static Cursor selectCursor(Select select, Class<?> classDef, DatabaseProvider databaseProvider, boolean firstOnly) {

        DbModelDescriptor dbModelDescriptor = getTableDescription(classDef, databaseProvider);

        return databaseProvider.query(
                dbModelDescriptor.getTableDbName(),
                select.getJoins() != null ? dbModelDescriptor.getColumnNamesWithTablePrefix() : dbModelDescriptor.getColumnNames(),
                select.getClause(),
                select.getJoins(),
                null,
                null,
                select.getOrderBy(),
                // small optimisation for local SQLite database (use a LIMIT to avoid fetching everything)
                firstOnly && databaseProvider instanceof BaseLocalDatabaseProvider ? new Limit(0, 1) : select.getLimit()
        );
    }

    protected static <T> CursorResult<T> select(Select select, Class<T> classDef, DatabaseProvider databaseProvider) {
        Cursor cursor = selectCursor(select, classDef, databaseProvider, false);
        return new CursorResult<>(classDef, databaseProvider.getResolver(), cursor);
    }

    protected static <T> T[] selectAndInit(Select select, Class<T> classDef, DatabaseProvider databaseProvider) {
        Cursor cursor = selectCursor(select, classDef, databaseProvider, false);

        Resolver resolver = databaseProvider.getResolver();
        T[] result = new CursorResult<>(classDef, resolver, cursor).toArray();

        for (T object : result) {
            resolver.initModelWithInitMethods(object);
        }

        return result;
    }

    protected static <T> T selectFirst(Select select, Class<T> classDef, DatabaseProvider databaseProvider) {
        Cursor cursor = selectCursor(select, classDef, databaseProvider, true);

        T[] results = getTableDescription(classDef, databaseProvider).getArrayResult(cursor);

        if (results != null && results.length > 0) {
            return results[0];
        } else {
            return null;
        }
    }

    protected static <T> T selectFirstAndInit(Select select, Class<T> classDef, DatabaseProvider databaseProvider) {
        Cursor cursor = selectCursor(select, classDef, databaseProvider, true);

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
            String uriSuffix = null;
            ContentValues[] valuesArray = new ContentValues[models.length];
            Where[][] conditionsArray = new Where[models.length][];

            for (int i = 0; i < models.length; i++) {
                Object model = models[i];

                if (models.length == 1) {
                    Object primaryKeyValue = tableDesc.getPrimaryKeyValue(model);
                    if (primaryKeyValue != null) {
                        uriSuffix = Uri.encode(primaryKeyValue.toString());
                    }
                }

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
                    uriSuffix,
                    valuesArray,
                    conditionsArray,
                    update.getConflictResolution()
            );
        } else {
            return databaseProvider.bulkUpdate(
                    getTableDescription(classDef, databaseProvider).getTableDbName(),
                    null,
                    new ContentValues[]{update.getContentValues()},
                    new Where[][]{update.getConditions()},
                    update.getConflictResolution()
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
            String uriSuffix = null;
            if (TextUtils.isEmpty(primaryKeyName)) {
                throw new IllegalStateException("delete with model() method require a primary key");
            }

            Object[] keys = new Object[delete.getModels().length];
            for (int i = 0; i < models.length; i++) {
                keys[i] = tableDesc.getPrimaryKeyValue(models[i]);

                if (models.length == 1 && keys[i] != null) {
                    uriSuffix = Uri.encode(keys[i].toString());
                }

                if (models[i] instanceof ModelListener) {
                    ((ModelListener) models[i]).onPreDelete();
                }
            }

            Where where = Where.field(primaryKeyName).isIn(keys);

            return databaseProvider.delete(
                    tableDesc.getTableDbName(),
                    uriSuffix,
                    new Where[]{where}
            );
        } else {
            return databaseProvider.delete(
                    getTableDescription(classDef, databaseProvider).getTableDbName(),
                    null,
                    delete.getConditions()
            );
        }
    }

    protected static
    @Nullable
    Cursor rawQuery(@NonNull String query, @NonNull DatabaseProvider databaseProvider) {
        return databaseProvider.rawQuery(query);
    }

    protected static
    @NonNull
    <T> rx.Single<T> wrapRx(@NonNull final Callable<T> func) {
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

    protected static
    @NonNull
    <T> Single<T> wrapRx2(@NonNull final Callable<T> func) {
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
