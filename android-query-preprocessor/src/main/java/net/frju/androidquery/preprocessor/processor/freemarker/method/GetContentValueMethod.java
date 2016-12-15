package net.frju.androidquery.preprocessor.processor.freemarker.method;

import net.frju.androidquery.preprocessor.processor.data.Data;
import net.frju.androidquery.preprocessor.processor.data.DbField;
import net.frju.androidquery.preprocessor.processor.data.TypeConverter;
import net.frju.androidquery.preprocessor.processor.utils.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import freemarker.ext.beans.StringModel;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;

public class GetContentValueMethod implements TemplateMethodModelEx {

    private static final String GET_CONTENT_VALUE = "getContentValue";

    private final Data mData;

    public static Map<String, Object> getMethodMap(Data data) {
        Map<String, Object> map = new HashMap<>();
        map.put(GET_CONTENT_VALUE, new GetContentValueMethod(data));
        return map;
    }

    private GetContentValueMethod(Data data) {
        mData = data;
    }

    private String assembleContentValue(String varName, DbField dbField) {
        String getter = StringUtils.getGetter(varName, dbField);

        TypeConverter converter = mData.getConverterFromClass(dbField.getType());
        if (converter != null) {
            return "new " + converter.getName() + "().convertToDb(" + getter + ")";
        }

        return getter;
    }

    @Override
    public Object exec(List arguments) throws TemplateModelException {
        Object var = arguments.get(0);
        String varName = var instanceof SimpleScalar ?
                var.toString() :
                String.valueOf(var);

        Object columnValue = arguments.get(1);

        DbField dbField;
        if (columnValue instanceof StringModel) {
            StringModel stringModel = (StringModel) columnValue;
            dbField = (DbField) stringModel.getAdaptedObject(DbField.class);
        } else {
            throw new IllegalStateException("The assembleContentValue argument must be type of " +
                    "net.frju.androidquery.preprocessor.processor.data.DbField");
        }

        return assembleContentValue(varName, dbField);
    }
}
