package net.frju.androidquery.preprocessor.processor.utils;

import net.frju.androidquery.preprocessor.processor.data.Data;
import net.frju.androidquery.preprocessor.processor.data.TypeConverter;

public class StringUtils {

    public static String firstToUpperCase(String value) {
        return Character.toUpperCase(value.charAt(0)) + value.substring(1);
    }

    public static String firstToLowerCase(String value) {
        return Character.toLowerCase(value.charAt(0)) + value.substring(1);
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
}
