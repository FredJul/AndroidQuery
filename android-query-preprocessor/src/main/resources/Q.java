package ${package_name};

import android.content.Context;
import android.database.Cursor;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
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

        private static HashMap<Class<?>, BaseLocalDatabaseProvider> mLocalProviders = new HashMap<>();
        private static HashMap<Class<?>, BaseContentDatabaseProvider> mContentProviders = new HashMap<>();

        public void init(Context context) {
            <#list tables as table>
            <#if table.getLocalDatabaseProvider().toString() != "java.lang.Void">
                    mLocalProviders.put(${table.getPackage()}.${table.getName()}.class, new ${table.getLocalDatabaseProvider().toString()}(context.getApplicationContext()));
            </#if>
            <#if table.getContentDatabaseProvider().toString() != "java.lang.Void">
                    mContentProviders.put(${table.getPackage()}.${table.getName()}.class, new ${table.getContentDatabaseProvider().toString()}(context.getContentResolver()));
            </#if>
            </#list>
        }

        @Override
        public TableDescription getTableDescription(Class<?> classDef) {
            <#assign isAssignableFrom>
                <#list tables as table>
                } else if (classDef.isAssignableFrom(${table.getPackage()}.${table.getName()}.class)) {
                    return s${table.getName()};
                </#list>
                }
            </#assign>

            ${isAssignableFrom?trim?remove_beginning("} else ")} else {
                throw new IllegalStateException("Please ensure all SQL tables are annotated with @Table");
            }
        }

        @Override
        public Class<?>[] getModelsForProvider(Class<? extends DatabaseProvider> providerClass) {
            ArrayList<Class<?>> result = new ArrayList<>();

            <#list tables as table>
            if (${table.getLocalDatabaseProvider().toString()}.class.equals(providerClass) || ${table.getContentDatabaseProvider().toString()}.class.equals(providerClass)) {
                result.add(${table.getPackage()}.${table.getName()}.class);
            }
            </#list>

            if (result.size() == 0) {
                throw new IllegalStateException("This provider does not have any @Table models registered into that resolver");
            }

            return result.toArray(new Class<?>[result.size()]);
        }

        @Override
        public BaseLocalDatabaseProvider getLocalDatabaseProviderForModel(Class<?> model) {
            return mLocalProviders.get(model);
        }

        @Override
        public BaseContentDatabaseProvider getContentDatabaseProviderForModel(Class<?> model) {
            return mContentProviders.get(model);
        }
    }

    <#list tables as table>

        <#assign getColumnNames>
            <#list table.getMutableColumns(tables) as column>
                "${column.getRealName()}",
            </#list>
        </#assign>
        <#assign getColumnNamesWithTablePrefix>
            <#list table.getMutableColumns(tables) as column>
                "${table.getRealName()}.${column.getRealName()}",
            </#list>
        </#assign>

        <#assign unionInsertColumnNames><#list table.getMutableColumns(tables) as column>${column.getRealName()},</#list></#assign>

        <#assign packagedTableName>
            ${table.getPackage()}.${table.getName()}
        </#assign>

        public static class ${table.getName()} implements TableDescription {

            <#list table.getColumns() as column>
                public static final String ${formatConstant(column.getName())} = "${column.getRealName()}";
            </#list>

            @Override
            public String getTableRealName() {
                return "${table.getRealName()}";
            }

            @Override
            public String getTableInsertQuery() {
                return ${assembleCreateTable(table, tables)}
            }

            @Override
            public String getPrimaryKeyRealName() {
                return "${table.getPrimaryKeyRealName()}";
            }

            @Override
            public String[] getIndexNames() {
                return new String[]{
                <#list table.getMutableColumns(tables) as column>
                    <#if column.isIndex()>
                        "${table.getName()}_${column.getName()}_index",
                    </#if>
                </#list>
                };
            }

            @Override
            public String getCreateIndexQuery() {
                StringBuilder sb = new StringBuilder();

                <#list table.getMutableColumns(tables) as column>
                    <#if column.isIndex()>
                        sb.append("CREATE INDEX ${table.getName()}_${column.getName()}_index ON ${table.getRealName()} (${column.getRealName()});");
                    </#if>
                </#list>

                return (sb.length() > 0) ? sb.toString() : null;
            }

            @Override
            public ${packagedTableName} getSingleResult(Cursor cursor) {
                ${packagedTableName} ${table.getName()?lower_case} = new ${packagedTableName}();

                ${joinReferences(table.getName(),tables)}

                for (int x = 0; x < cursor.getColumnCount(); x++) {
                    <#assign retrieveSQLSelectResults>
                        <#list table.getColumns() as column>
                            <#if column.isJoinable(tables)>
                                ${join(column.getClassName(),tables)}
                            <#else>
                                } else if (cursor.getColumnName(x).equals(${formatConstant(column.getName())})) {
                                    ${table.getName()?lower_case}.${column.getName()} = ${getCursorGetter(column.getType())};
                            </#if>
                        </#list>
                        }
                    </#assign>

                    ${retrieveSQLSelectResults?trim?remove_beginning("} else ")}
                }

                return ${table.getName()?lower_case};
            }

            @Override
            public ${packagedTableName}[] getArrayResult(Cursor cursor) {
                ${packagedTableName}[] result = new ${packagedTableName}[cursor.getCount()];

                cursor.moveToFirst();
                for (int i = 0; !cursor.isAfterLast(); i++) {
                    result[i] = getSingleResult(cursor);
                    cursor.moveToNext();
                }

                cursor.close();

                return result;
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
            public Object getPrimaryKeyValue(Object model) {
                ${packagedTableName} ${table.getName()?lower_case} = (${packagedTableName})model;

                return ${getPrimaryKeyValue(table.getName()?lower_case, table)};
            }

            @Override
            public ContentValues getContentValues(Object model) {
                ${packagedTableName} ${table.getName()?lower_case} = (${packagedTableName})model;

                ContentValues contentValues = new ContentValues();

                <#list table.getMutableColumns(tables) as column>
                    contentValues.put(${formatConstant(column.getName())}, ${getContentValue(table.getName()?lower_case, column)});
                </#list>

                return contentValues;
            }

            <#if table.getContentDatabaseProvider().toString() != "java.lang.Void">
            public static Uri getContentUri() {
                return Q.getResolver().getContentDatabaseProviderForModel(${packagedTableName}.class).getUri(${packagedTableName}.class);
            }
            </#if>

            public static Count.Builder<${packagedTableName}> count() {
                <#if table.getLocalDatabaseProvider().toString() != "java.lang.Void">
                return Count.getBuilder(${packagedTableName}.class, Q.getResolver().getLocalDatabaseProviderForModel(${packagedTableName}.class));
                <#else>
                return Count.getBuilder(${packagedTableName}.class, Q.getResolver().getContentDatabaseProviderForModel(${packagedTableName}.class));
                </#if>
            }

            public static Select.Builder<${packagedTableName}> select() {
                <#if table.getLocalDatabaseProvider().toString() != "java.lang.Void">
                return Select.getBuilder(${packagedTableName}.class, Q.getResolver().getLocalDatabaseProviderForModel(${packagedTableName}.class));
                <#else>
                return Select.getBuilder(${packagedTableName}.class, Q.getResolver().getContentDatabaseProviderForModel(${packagedTableName}.class));
                </#if>
            }

            <#if table.getLocalDatabaseProvider().toString() != "java.lang.Void">
            public static Delete.Builder<${packagedTableName}> delete() {
                return Delete.getBuilder(${packagedTableName}.class, Q.getResolver().getLocalDatabaseProviderForModel(${packagedTableName}.class));
            }
            </#if>
            <#if table.getContentDatabaseProvider().toString() != "java.lang.Void">
            public static Delete.Builder<${packagedTableName}> deleteViaContentProvider() {
                return Delete.getBuilder(${packagedTableName}.class, Q.getResolver().getContentDatabaseProviderForModel(${packagedTableName}.class));
            }
            </#if>

            <#if table.getLocalDatabaseProvider().toString() != "java.lang.Void">
            public static Update.Builder<${packagedTableName}> update() {
                return Update.getBuilder(${packagedTableName}.class, Q.getResolver().getLocalDatabaseProviderForModel(${packagedTableName}.class));
            }
            </#if>
            <#if table.getContentDatabaseProvider().toString() != "java.lang.Void">
            public static Update.Builder<${packagedTableName}> updateViaContentProvider() {
                return Update.getBuilder(${packagedTableName}.class, Q.getResolver().getContentDatabaseProviderForModel(${packagedTableName}.class));
            }
            </#if>

            <#if table.getLocalDatabaseProvider().toString() != "java.lang.Void">
            public static Save.Builder<${packagedTableName}> save(${packagedTableName}... models) {
                return Save.getBuilder(Q.getResolver().getLocalDatabaseProviderForModel(${packagedTableName}.class), models);
            }
            </#if>
            <#if table.getContentDatabaseProvider().toString() != "java.lang.Void">
            public static Save.Builder<${packagedTableName}> saveViaContentProvider(${packagedTableName}... models) {
                return Save.getBuilder(Q.getResolver().getContentDatabaseProviderForModel(${packagedTableName}.class), models);
            }
            </#if>

            <#if table.getLocalDatabaseProvider().toString() != "java.lang.Void">
            public static Insert.Builder<${packagedTableName}> insert(${packagedTableName}... models) {
                return Insert.getBuilder(Q.getResolver().getLocalDatabaseProviderForModel(${packagedTableName}.class), models);
            }
            </#if>
            <#if table.getContentDatabaseProvider().toString() != "java.lang.Void">
            public static Insert.Builder<${packagedTableName}> insertViaContentProvider(${packagedTableName}... models) {
                return Insert.getBuilder(Q.getResolver().getContentDatabaseProviderForModel(${packagedTableName}.class), models);
            }
            </#if>

            <#if table.getLocalDatabaseProvider().toString() != "java.lang.Void">
            public static Raw.Builder raw() {
                return Raw.getBuilder(Q.getResolver().getLocalDatabaseProviderForModel(${packagedTableName}.class));
            }
            </#if>

            public static Result<${packagedTableName}> fromCursor(Cursor cursor) {
                return new Result<>(${packagedTableName}.class, Q.getResolver(), cursor);
            }
        }

        private static ${table.getName()} s${table.getName()} = new ${table.getName()}();

    </#list>
}