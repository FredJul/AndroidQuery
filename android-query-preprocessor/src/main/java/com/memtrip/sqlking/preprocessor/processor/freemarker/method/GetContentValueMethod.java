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

public class GetContentValueMethod implements TemplateMethodModelEx {

    private static final String GET_CONTENT_VALUE = "getContentValue";

    private Data mData;

    public static Map<String, Object> getMethodMap(Data data) {
        Map<String, Object> map = new HashMap<>();
        map.put(GET_CONTENT_VALUE, new GetContentValueMethod(data));
        return map;
    }

    private GetContentValueMethod(Data data) {
        mData = data;
    }

    private String assembleContentValue(String varName, Column column) {
        TypeConverter converter = mData.getConverterFromClass(column.getType());
        if (converter != null) {
            return "new " + converter.getName() + "().convertToDb(" + varName + "." + column.getName() + ")";
        }

        return varName + "." + column.getName();
    }

    @Override
    public Object exec(List arguments) throws TemplateModelException {
        Object var = arguments.get(0);
        String varName = var instanceof SimpleScalar ?
                var.toString() :
                String.valueOf(var);

        Object columnValue = arguments.get(1);

        Column column;
        if (columnValue instanceof StringModel) {
            StringModel stringModel = (StringModel) columnValue;
            column = (Column) stringModel.getAdaptedObject(Column.class);
        } else {
            throw new IllegalStateException("The assembleContentValue argument must be type of " +
                    "com.memtrip.sqlking.preprocessor.processor.data.Column");
        }

        return assembleContentValue(varName, column);
    }
}
