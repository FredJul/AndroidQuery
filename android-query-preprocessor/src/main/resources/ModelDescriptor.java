package ${package_name};

import android.database.Cursor;
import android.content.ContentValues;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import net.frju.androidquery.database.*;
import net.frju.androidquery.operation.function.*;

import java.util.List;

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

public class ${formatConstant(table.getName())} implements DbModelDescriptor {

    <#list table.getFields() as column>
    public static final String ${formatConstant(column.getName())} = "${column.getDbName()}";
    </#list>

    <#if table.hasLocalDatabaseProvider()>
    public static class ContentProvider extends BaseContentProvider {

        @Override
        protected @NonNull BaseLocalDatabaseProvider getLocalSQLProvider() {
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
    public @Nullable String getPrimaryKeyDbName() {
        return "${table.getPrimaryKeyDbName()}";
    }

    @Override
    public @NonNull String[] getIndexNames() {
        return new String[]{
        <#list table.getMutableFields(tables) as column>
            <#if column.isIndex()>
                "${table.getName()}_${column.getName()}_index",
            </#if>
        </#list>
        };
    }

    @Override
    public @Nullable String getCreateIndexQuery() {
        StringBuilder sb = new StringBuilder();

        <#list table.getMutableFields(tables) as column>
            <#if column.isIndex()>
                sb.append("CREATE INDEX ${table.getName()}_${column.getName()}_index ON ${table.getDbName()} (${column.getDbName()});");
            </#if>
        </#list>

        return (sb.length() > 0) ? sb.toString() : null;
    }

    @Override
    public @Nullable ${packagedTableName} getSingleResult(@Nullable Cursor cursor) {
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
    public @NonNull ${packagedTableName}[] getArrayResult(@Nullable Cursor cursor) {
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
    public @NonNull String[] getColumnNames() {
        return new String[]{${getColumnNames?remove_ending(",")}};
    }

    @Override
    public @NonNull String[] getColumnNamesWithTablePrefix() {
        return new String[]{${getColumnNamesWithTablePrefix?remove_ending(",")}};
    }

    @Override
    public @Nullable Object getPrimaryKeyValue(@NonNull Object model) {
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
        return Q.getResolver().getDatabaseProviderForModel(${packagedTableName}.class).getUri(${packagedTableName}.class, null);
    }

    <#if table.hasPrimaryKey()>
    public static @NonNull Uri getContentUri(@Nullable ${packagedTableName} model) {
        return Q.getResolver().getDatabaseProviderForModel(${packagedTableName}.class).getUri(${packagedTableName}.class, Uri.encode(${getPrimaryKeyValue("model", table)}.toString()));
    }
    </#if>

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

    public static @NonNull CursorResult<${packagedTableName}> fromCursor(@Nullable Cursor cursor) {
        return new CursorResult<>(${packagedTableName}.class, Q.getResolver(), cursor);
    }
}
