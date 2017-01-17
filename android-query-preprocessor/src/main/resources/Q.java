package ${package_name};

import android.content.Context;
import android.database.Cursor;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.support.annotation.NonNull;
import net.frju.androidquery.database.*;
import net.frju.androidquery.operation.function.*;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

public class Q {

    private static DefaultResolver sResolver;

    public static void init(Context context) {
        if (sResolver == null) {
            sResolver = new DefaultResolver();
            sResolver.init(context);
        }
    }

    public static DefaultResolver getResolver() {
        return sResolver;
    }

    public static class DefaultResolver implements Resolver {

        private static HashMap<Class<?>, DatabaseProvider> mProviders = new HashMap<>();

        public void init(@NonNull Context context) {
            HashMap<String, DatabaseProvider> providersByName = new HashMap<>();

            <#list providers as provider>
            providersByName.put("${provider.toString()}", new ${provider.toString()}(context.getApplicationContext()));
            </#list>

            <#list tables as table>
            <#if table.getDatabaseProvider().toString() != "java.lang.Void">
                    mProviders.put(${table.getPackage()}.${table.getName()}.class, providersByName.get("${table.getDatabaseProvider().toString()}"));
                    mProviders.put(${formatConstant(table.getName())}.class, providersByName.get("${table.getDatabaseProvider().toString()}")); // to be more error-tolerant
            </#if>
            </#list>
        }

        @Override
        public void initModelWithInitMethods(@NonNull Object model) {
            <#assign isAssignableFrom>
                <#list tables as table>
                else if (model instanceof ${table.getPackage()}.${table.getName()}) {
                    <#list table.getInitMethodNames() as initMethod>
                    ((${table.getPackage()}.${table.getName()})model).${initMethod}();
                    </#list>
                }
                </#list>
            </#assign>

            ${isAssignableFrom?trim?remove_beginning("else ")}
        }

        @Override
        public @NonNull Class<?> getModelClassFromName(@NonNull String modelDbName) {
            switch (modelDbName) {
            <#list tables as table>
            case "${table.getDbName()}":
                return ${table.getPackage()}.${table.getName()}.class;
            </#list>
            default:
                throw new IllegalStateException("The modelDbName " + modelDbName + " is not a correct");
            }
        }

        @Override
        public @NonNull DbModelDescriptor getDbModelDescriptor(@NonNull Class<?> classDef) {
            <#assign isAssignableFrom>
                <#list tables as table>
                } else if (classDef.isAssignableFrom(${table.getPackage()}.${table.getName()}.class)) {
                    return s${table.getName()};
                </#list>
                }
            </#assign>

            ${isAssignableFrom?trim?remove_beginning("} else ")} else {
                throw new IllegalStateException("Please ensure all SQL tables are annotated with @DbModel");
            }
        }

        @Override
        public @NonNull Class<?>[] getModelsForProvider(Class<? extends DatabaseProvider> providerClass) {
            ArrayList<Class<?>> result = new ArrayList<>();

            <#list tables as table>
            if (${table.getDatabaseProvider().toString()}.class.equals(providerClass)) {
                result.add(${table.getPackage()}.${table.getName()}.class);
            }
            </#list>

            if (result.size() == 0) {
                throw new IllegalStateException("This provider does not have any @DbModel models registered into that resolver");
            }

            return result.toArray(new Class<?>[result.size()]);
        }

        @Override
        public DatabaseProvider getDatabaseProviderForModel(Class<?> model) {
            return mProviders.get(model);
        }
    }

    <#list tables as table>

        <#assign getColumnNames>
            <#list table.getMutableFields(tables) as column>
                "${column.getDbName()}",
            </#list>
        </#assign>
        <#assign getColumnNamesWithTablePrefix>
            <#list table.getMutableFields(tables) as column>
                "${table.getDbName()}.${column.getDbName()}",
            </#list>
        </#assign>

        <#assign unionInsertColumnNames><#list table.getMutableFields(tables) as column>${column.getDbName()},</#list></#assign>

        <#assign packagedTableName>
            ${table.getPackage()}.${table.getName()}
        </#assign>

        public static class ${formatConstant(table.getName())} implements DbModelDescriptor {

            <#list table.getFields() as column>
            public static final String ${formatConstant(column.getName())} = "${column.getDbName()}";
            </#list>

            <#if table.hasLocalDatabaseProvider()>
            public static class ContentProvider extends BaseContentProvider {

                @Override
                protected BaseLocalDatabaseProvider getLocalSQLProvider() {
                    Q.init(getContext());
                    return (BaseLocalDatabaseProvider) Q.getResolver().getDatabaseProviderForModel(${formatConstant(table.getName())}.class);
                }
            }
            </#if>

            @Override
            public @NonNull String getTableDbName() {
                return "${table.getDbName()}";
            }

            @Override
            public @NonNull String getTableCreateQuery() {
                return ${assembleCreateTable(table, tables)}
            }

            @Override
            public @NonNull String[] getColumnsSqlArray() {
                return ${getColumnsSqlArray(table, tables)};
            }

            @Override
            public String getPrimaryKeyDbName() {
                return "${table.getPrimaryKeyDbName()}";
            }

            @Override
            public String[] getIndexNames() {
                return new String[]{
                <#list table.getMutableFields(tables) as column>
                    <#if column.isIndex()>
                        "${table.getName()}_${column.getName()}_index",
                    </#if>
                </#list>
                };
            }

            @Override
            public String getCreateIndexQuery() {
                StringBuilder sb = new StringBuilder();

                <#list table.getMutableFields(tables) as column>
                    <#if column.isIndex()>
                        sb.append("CREATE INDEX ${table.getName()}_${column.getName()}_index ON ${table.getDbName()} (${column.getDbName()});");
                    </#if>
                </#list>

                return (sb.length() > 0) ? sb.toString() : null;
            }

            @Override
            public ${packagedTableName} getSingleResult(Cursor cursor) {
                if (cursor != null){
                    ${packagedTableName} ${table.getName()?lower_case} = new ${packagedTableName}();

                    ${joinReferences(table.getName(),tables)}

                    for (int x = 0; x < cursor.getColumnCount(); x++) {
                        <#assign retrieveSQLSelectResults>
                            <#list table.getFields() as column>
                                <#if column.isJoinable(tables)>
                                    ${join(column.getClassName(),tables)}
                                <#else>
                                    } else if (cursor.getColumnName(x).equals(${formatConstant(column.getName())})) {
                                        ${getColumnSetter(table.getName()?lower_case, getCursorGetter(column.getType()), column)};
                                </#if>
                            </#list>
                            }
                        </#assign>

                        ${retrieveSQLSelectResults?trim?remove_beginning("} else ")}
                    }

                    return ${table.getName()?lower_case};
                }

                return null;
            }

            @Override
            public ${packagedTableName}[] getArrayResult(Cursor cursor) {
                if (cursor != null){
                    ${packagedTableName}[]result = new ${packagedTableName}[cursor.getCount()];

                    cursor.moveToFirst();
                    for(int i=0;!cursor.isAfterLast();i++){
                        result[i]=getSingleResult(cursor);
                        cursor.moveToNext();
                    }

                    cursor.close();

                    return result;
                }

                return new ${packagedTableName}[]{}; // empty array
            }

            @Override
            public String[] getColumnNames() {
                return new String[]{${getColumnNames?remove_ending(",")}};
            }

            @Override
            public String[] getColumnNamesWithTablePrefix() {
                return new String[]{${getColumnNamesWithTablePrefix?remove_ending(",")}};
            }

            @Override
            public Object getPrimaryKeyValue(@NonNull Object model) {
                ${packagedTableName} ${table.getName()?lower_case} = (${packagedTableName})model;

                return ${getPrimaryKeyValue(table.getName()?lower_case, table)};
            }

            @Override
            public void setIdToModel(@NonNull Object model, long id) {
                <#if table.getPrimaryKeyName() != "" && isPrimaryKeyAutoIncrement(table) == "true" && (table.getPrimaryKeyType() == "java.lang.Long" || table.getPrimaryKeyType() == "long")>
                ${packagedTableName} ${table.getName()?lower_case} = (${packagedTableName})model;
                ${getPrimaryKeySetter(table.getName()?lower_case, "id", table)};
                </#if>
            }

            @Override
            public boolean isPrimaryKeyAutoIncrement() {
                return ${isPrimaryKeyAutoIncrement(table)};
            }

            @Override
            public @NonNull ContentValues getContentValues(@NonNull Object model) {
                ${packagedTableName} ${table.getName()?lower_case} = (${packagedTableName})model;

                ContentValues contentValues = new ContentValues();

                <#list table.getMutableFields(tables) as column>
                    <#if !column.hasAutoIncrement()>
                    contentValues.put(${formatConstant(column.getName())}, ${getContentValue(table.getName()?lower_case, column)});
                    </#if>
                </#list>

                return contentValues;
            }

            public static @NonNull Uri getContentUri() {
                return Q.getResolver().getDatabaseProviderForModel(${packagedTableName}.class).getUri(${packagedTableName}.class);
            }

            public static @NonNull Count.Builder<${packagedTableName}> count() {
                return Count.getBuilder(${packagedTableName}.class, Q.getResolver().getDatabaseProviderForModel(${packagedTableName}.class));
            }

            public static @NonNull Select.Builder<${packagedTableName}> select() {
                return Select.getBuilder(${packagedTableName}.class, Q.getResolver().getDatabaseProviderForModel(${packagedTableName}.class));
            }

            public static @NonNull Delete.Builder<${packagedTableName}> delete() {
                return Delete.getBuilder(${packagedTableName}.class, Q.getResolver().getDatabaseProviderForModel(${packagedTableName}.class));
            }

            public static @NonNull Update.Builder<${packagedTableName}> update() {
                return Update.getBuilder(${packagedTableName}.class, Q.getResolver().getDatabaseProviderForModel(${packagedTableName}.class));
            }

            public static @NonNull Save.Builder<${packagedTableName}> save(@NonNull ${packagedTableName}... models) {
                return Save.getBuilder(Q.getResolver().getDatabaseProviderForModel(${packagedTableName}.class), models);
            }
            public static @NonNull Save.Builder<${packagedTableName}> save(@NonNull List<${packagedTableName}> models) {
                return Save.getBuilder(Q.getResolver().getDatabaseProviderForModel(${packagedTableName}.class), models);
            }

            public static @NonNull Insert.Builder<${packagedTableName}> insert(@NonNull ${packagedTableName}... models) {
                return Insert.getBuilder(Q.getResolver().getDatabaseProviderForModel(${packagedTableName}.class), models);
            }
            public static @NonNull Insert.Builder<${packagedTableName}> insert(@NonNull List<${packagedTableName}> models) {
                return Insert.getBuilder(Q.getResolver().getDatabaseProviderForModel(${packagedTableName}.class), models);
            }

            <#if table.hasLocalDatabaseProvider()>
            public static @NonNull Raw.Builder raw(@NonNull String query) {
                return Raw.getBuilder(Q.getResolver().getDatabaseProviderForModel(${packagedTableName}.class), query);
            }
            </#if>

            public static @NonNull CursorResult<${packagedTableName}> fromCursor(Cursor cursor) {
                return new CursorResult<>(${packagedTableName}.class, Q.getResolver(), cursor);
            }
        }

        private static ${formatConstant(table.getName())} s${table.getName()} = new ${formatConstant(table.getName())}();

    </#list>
}