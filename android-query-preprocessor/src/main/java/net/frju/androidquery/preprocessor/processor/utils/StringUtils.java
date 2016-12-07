package net.frju.androidquery.preprocessor.processor.utils;

import net.frju.androidquery.preprocessor.processor.data.Column;
import net.frju.androidquery.preprocessor.processor.data.Data;
import net.frju.androidquery.preprocessor.processor.data.Table;
import net.frju.androidquery.preprocessor.processor.data.TypeConverter;

import java.util.List;

public class StringUtils {

    private static final String SQL_TEXT = "text";
    private static final String SQL_INTEGER = "integer";
    private static final String SQL_REAL = "real";
    private static final String SQL_BLOB = "blob";

    public static String firstToUpperCase(String value) {
        return Character.toUpperCase(value.charAt(0)) + value.substring(1);
    }

    public static String firstToLowerCase(String value) {
        return Character.toLowerCase(value.charAt(0)) + value.substring(1);
    }

    public static String columnToSql(Data data, List<Table> tables, Column column) {
        if (!column.isJoinable(tables)) {
            StringBuilder statementBuilder = new StringBuilder();

            statementBuilder.append(column.getRealName());
            statementBuilder.append(" ");

            String sqlType = StringUtils.getSQLDataTypeFromClassRef(column.getType());
            if (sqlType.length() == 0) {
                TypeConverter converter = data.getConverterFromClass(column.getType());
                if (converter != null) {
                    sqlType = StringUtils.getSQLDataTypeFromClassRef(converter.getDbClassName());
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

            return statementBuilder.toString();
        }

        return null;
    }

    public static String assembleTypeGetter(Data data, String type) {
        String sqlType = type;
        TypeConverter converter = data.getConverterFromClass(type);
        if (converter != null) {
            sqlType = converter.getDbClassName();
        }

        String getter;
        switch (sqlType) {
            case "java.lang.String":
                getter = "cursor.getString(x)";
                break;
            case "java.lang.Long":
            case "long":
                getter = "cursor.getLong(x)";
                break;
            case "java.lang.Integer":
            case "int":
                getter = "cursor.getInt(x)";
                break;
            case "java.lang.Short":
            case "short":
                getter = "cursor.getShort(x)";
                break;
            case "java.lang.Boolean":
            case "boolean":
                getter = "cursor.getInt(x) == 1 ? true : false";
                break;
            case "java.lang.Double":
            case "double":
                getter = "cursor.getDouble(x)";
                break;
            case "java.lang.Float":
            case "float":
                getter = "cursor.getFloat(x)";
                break;
            case "byte[]":
                getter = "cursor.getBlob(x)";
                break;
            default:
                getter = ""; // TODO: foreign key object
        }

        if (converter != null) {
            getter = "new " + converter.getName() + "().convertFromDb(" + getter + ")";
        }

        return getter;
    }

    /**
     * Determine the data type of the provided class reference and return
     * the associated SQL data type
     *
     * @param value The class reference
     * @return The SQL data type to return
     */
    public static String getSQLDataTypeFromClassRef(String value) {
        switch (value) {
            case "java.lang.String":
                return SQL_TEXT;
            case "java.lang.Long":
            case "long":
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
}
