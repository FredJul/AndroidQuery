package net.frju.androidquery.preprocessor.processor.utils;

import net.frju.androidquery.preprocessor.processor.data.Data;
import net.frju.androidquery.preprocessor.processor.data.DbField;
import net.frju.androidquery.preprocessor.processor.data.DbModel;
import net.frju.androidquery.preprocessor.processor.data.TypeConverter;

import java.util.List;

public class StringUtils {

    private static final String SQL_TEXT = "text";
    private static final String SQL_INTEGER = "integer";
    private static final String SQL_REAL = "real";
    private static final String SQL_BLOB = "blob";

    public static String join(List<String> list, String conjunction) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String item : list) {
            if (first) {
                first = false;
            } else {
                sb.append(conjunction);
            }
            sb.append(item);
        }
        return sb.toString();
    }

    public static String firstToUpperCase(String value) {
        return Character.toUpperCase(value.charAt(0)) + value.substring(1);
    }

    public static String firstToLowerCase(String value) {
        return Character.toLowerCase(value.charAt(0)) + value.substring(1);
    }

    public static String columnToSql(Data data, List<DbModel> dbModels, DbField dbField) {
        if (!dbField.isJoinable(dbModels)) {
            StringBuilder statementBuilder = new StringBuilder();

            statementBuilder.append(dbField.getDbName());
            statementBuilder.append(" ");

            String sqlType = StringUtils.getSQLDataTypeFromClassRef(dbField.getType());
            if (sqlType.length() == 0) {
                TypeConverter converter = data.getConverterFromClass(dbField.getType());
                if (converter != null) {
                    sqlType = StringUtils.getSQLDataTypeFromClassRef(converter.getDbClassName());
                }
            }
            statementBuilder.append(sqlType);

            if (dbField.hasPrimaryKey()) {
                statementBuilder.append(" PRIMARY KEY");
                if (dbField.hasAutoIncrement()) {
                    statementBuilder.append(" AUTOINCREMENT");
                }
            } else if (dbField.isNotNull()) {
                statementBuilder.append(" NOT NULL");
            } else if (dbField.isUnique() && dbField.uniqueGroup() == -1) {
                statementBuilder.append(" UNIQUE");
            }

            return statementBuilder.toString();
        }

        return null;
    }

    public static String getGetter(String varName, DbField dbField) {
        String getter = varName + "." + dbField.getName();
        if (dbField.getGetterName() != null && dbField.getGetterName().length() > 0) {
            getter = varName + "." + dbField.getGetterName() + "()";
        } else if (!dbField.isIsPublicField()) {
            if (dbField.getName().charAt(0) == 'i' && dbField.getName().charAt(1) == 's'
                    && Character.isUpperCase(dbField.getName().charAt(2))) {
                getter = varName + "." + dbField.getName() + "()";
            } else {
                getter = varName + ".get" + StringUtils.firstToUpperCase(dbField.getName()) + "()";
            }
        }
        return getter;
    }

    public static String getSetter(String varName, String valueVarName, DbField dbField) {
        String setter = varName + "." + dbField.getName() + " = " + valueVarName;
        if (dbField.getSetterName() != null && dbField.getSetterName().length() > 0) {
            setter = varName + "." + dbField.getSetterName() + "(" + valueVarName + ")";
        } else if (!dbField.isIsPublicField()) {
            if (dbField.getName().charAt(0) == 'i' && dbField.getName().charAt(1) == 's'
                    && Character.isUpperCase(dbField.getName().charAt(2))) {
                setter = varName + ".set" + dbField.getName().substring(2) + "(" + valueVarName + ")";
            } else {
                setter = varName + ".set" + StringUtils.firstToUpperCase(dbField.getName()) + "(" + valueVarName + ")";
            }
        }
        return setter;
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
                getter = "cursor.getInt(x) > 0 ? true : false";
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
