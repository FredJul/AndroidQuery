package net.frju.androidquery.preprocessor.processor.freemarker.method;

import net.frju.androidquery.preprocessor.processor.data.Column;
import net.frju.androidquery.preprocessor.processor.data.Data;
import net.frju.androidquery.preprocessor.processor.data.ForeignKey;
import net.frju.androidquery.preprocessor.processor.data.Table;
import net.frju.androidquery.preprocessor.processor.data.TypeConverter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import freemarker.ext.beans.StringModel;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;

public class AssembleCreateTableMethod implements TemplateMethodModelEx {

    private static final String ASSEMBLE_CREATE_TABLE = "assembleCreateTable";

    private static final String SQL_TEXT = "text";
    private static final String SQL_INTEGER = "integer";
    private static final String SQL_LONG = "long";
    private static final String SQL_REAL = "real";
    private static final String SQL_BLOB = "blob";

    private final Data mData;

    public static Map<String, Object> getMethodMap(Data data) {
        Map<String, Object> map = new HashMap<>();
        map.put(ASSEMBLE_CREATE_TABLE, new AssembleCreateTableMethod(data));
        return map;
    }

    private AssembleCreateTableMethod(Data data) {
        mData = data;
    }

    /**
     * Build a create table statement based on the provided tableName and members
     * @param	table	The table that the statement will create
     * @return	A SQL statement that will create a table
     */
    private String buildCreateTableStatement(Table table, List<Table> tables) {
        StringBuilder statementBuilder = new StringBuilder();

        statementBuilder.append("CREATE TABLE ");
        statementBuilder.append(table.getRealName());
        statementBuilder.append(" (");

        for (int i = 0; i < table.getColumns().size(); i++) {
            Column column = table.getColumns().get(i);

            if (!column.isJoinable(tables)) {
                statementBuilder.append(column.getRealName());
                statementBuilder.append(" ");
                String sqlType = getSQLDataTypeFromClassRef(column.getType());
                if (sqlType.length() == 0) {
                    TypeConverter converter = mData.getConverterFromClass(column.getType());
                    if (converter != null) {
                        sqlType = getSQLDataTypeFromClassRef(converter.getDbClassName());
                    }
                }
                statementBuilder.append(sqlType);

                if (column.hasPrimaryKey()) {
                    statementBuilder.append(" PRIMARY KEY");
                    if (column.hasAutoIncrement()) {
                        statementBuilder.append(" AUTOINCREMENT");
                    }
                } else if (column.isUnique()) {
                    statementBuilder.append(" UNIQUE");
                }

                statementBuilder.append(",");
            }
        }

        for (ForeignKey foreignKey : table.getForeignKeys()) {
            statementBuilder.append("FOREIGN KEY(")
                    .append(foreignKey.getThisColumn()).append(") REFERENCES ")
                    .append(foreignKey.getTable())
                    .append("(")
                    .append(foreignKey.getForeignColumn())
                    .append("),");
        }

        statementBuilder.deleteCharAt(statementBuilder.length()-1);

        statementBuilder.append(");");

        return "\"" + statementBuilder.toString() + "\";";
    }

    /**
     * Determine the data type of the provided class reference and return
     * the associated SQL data type
     * @param	value	The class reference
     * @return	The SQL data type to return
     */
    private String getSQLDataTypeFromClassRef(String value) {
        switch (value) {
            case "java.lang.String":
                return SQL_TEXT;
            case "java.lang.Long":
            case "long":
                return SQL_LONG;
            case "java.lang.Integer":
            case "java.lang.Short":
            case "int":
            case "short":
                return SQL_INTEGER;
            case "java.lang.Boolean":
            case "boolean":
                return SQL_INTEGER;
            case "java.lang.Float":
            case "java.lang.Double":
            case "float":
            case "double":
                return SQL_REAL;
            case "byte[]":
                return SQL_BLOB;
            default:
                return ""; // TODO: foreign key object
        }
    }

    @Override
    public Object exec(List arguments) throws TemplateModelException {
        Object tableNameValue = arguments.get(0);
        Object tablesValue = arguments.get(1);

        Table table;
        if (tableNameValue instanceof StringModel) {
            StringModel stringModel = (StringModel)tableNameValue;
            table = (Table)stringModel.getAdaptedObject(Table.class);
        } else {
            throw new IllegalStateException("The assembleCreateTable argument must be type of " +
                    "net.frju.androidquery.preprocessor.processor.data.Table");
        }

        List<Table> tables = Util.getTables(tablesValue);

        return buildCreateTableStatement(table, tables);
    }
}