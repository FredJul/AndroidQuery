package com.memtrip.sqlking.preprocessor.processor.freemarker.method;

import com.memtrip.sqlking.preprocessor.processor.data.Column;
import com.memtrip.sqlking.preprocessor.processor.data.Data;
import com.memtrip.sqlking.preprocessor.processor.data.TypeConverter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import freemarker.ext.beans.StringModel;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;

public class GetInsertValueMethod implements TemplateMethodModelEx {

    private static final String GET_INSERT_VALUE = "getInsertValue";

    private Data mData;

    public static Map<String, Object> getMethodMap(Data data) {
        Map<String, Object> map = new HashMap<>();
        map.put(GET_INSERT_VALUE, new GetInsertValueMethod(data));
        return map;
    }

    private GetInsertValueMethod(Data data) {
        mData = data;
    }

    private String assembleInsertValue(Column column, String getter) {
        if (column.hasPrimaryKey() && column.hasAutoIncrement()) {
            return "NULL";
        } else {
            String sqlType = column.getType();

            TypeConverter converter = mData.getConverterFromClass(column.getType());
            if (converter != null) {
                getter = "new " + converter.getName() + "().convertToDb(" + getter + ")";
                sqlType = converter.getDbClassName();
            }

            switch (sqlType) {
                case "java.lang.String":
                case "java.lang.Long":
                case "java.lang.Integer":
                case "java.lang.Double":
                case "java.lang.Float":
                case "long":
                case "int":
                case "double":
                case "float":
                    return "'\" + " + getter + " + \"'";
                case "boolean":
                case "java.lang.Boolean":
                    return "\" + (" + getter + " ? \"'1'\" : \"'0'\") + \"";
                case "byte[]":
                    return "\" + assembleBlob(" + getter + ") + \"";
                default:
                    return "";
            }
        }
    }

    @Override
    public Object exec(List arguments) throws TemplateModelException {
        Object columnValue = arguments.get(0);
        Object getter = arguments.get(1);

        Column column;
        if (columnValue instanceof StringModel) {
            StringModel stringModel = (StringModel)columnValue;
            column = (Column)stringModel.getAdaptedObject(Column.class);
        } else {
            throw new IllegalStateException("The getInsertValue argument must be type of " +
                    "com.memtrip.sqlking.preprocessor.processor.data.Column");
        }

        String getterValue = getter instanceof SimpleScalar ?
                getter.toString() :
                String.valueOf(getter);

        return assembleInsertValue(column, getterValue);
    }
}
