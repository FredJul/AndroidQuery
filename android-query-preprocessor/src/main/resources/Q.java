package ${package_name};

import android.content.Context;
import android.database.Cursor;
import android.content.ContentResolver;
import android.content.ContentValues;
import com.memtrip.sqlking.database.TableDescription;
import com.memtrip.sqlking.database.Resolver;
import com.memtrip.sqlking.database.LocalDatabaseProvider;
import com.memtrip.sqlking.database.ContentDatabaseProvider;

import java.util.List;
import java.util.ArrayList;

public class Q {

    public static class DefaultResolver implements Resolver {

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
    }

    public static class LocalDatabaseProvider extends com.memtrip.sqlking.database.LocalDatabaseProvider {

        public LocalDatabaseProvider(Context context,
                                     String name,
                                     int version,
                                     Class<?>... modelClassDef) {
            super(context, name, version, new DefaultResolver(), modelClassDef);
        }

    }

    public static class ContentDatabaseProvider extends com.memtrip.sqlking.database.ContentDatabaseProvider {

        public ContentDatabaseProvider(ContentResolver contentResolver, String authority) {
            super(contentResolver, authority, new DefaultResolver());
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

            private String assembleBlob(byte[] val) {
                if (val != null) {
                    StringBuilder sb = new StringBuilder();

                    for (byte b : val)
                        sb.append(String.format("%02X ", b));

                    return sb.toString();
                } else {
                    return "NULL";
                }
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
            public ContentValues getContentValues(Object model) {
                ${packagedTableName} ${table.getName()?lower_case} = (${packagedTableName})model;

                ContentValues contentValues = new ContentValues();

                <#list table.getMutableColumns(tables) as column>
                    contentValues.put(${formatConstant(column.getName())}, ${getContentValue(table.getName()?lower_case, column)});
                </#list>

                return contentValues;
            }
        }

        private static ${table.getName()} s${table.getName()} = new ${table.getName()}();

    </#list>
}