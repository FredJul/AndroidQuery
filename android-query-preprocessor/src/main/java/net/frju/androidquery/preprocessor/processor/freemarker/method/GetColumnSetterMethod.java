package net.frju.androidquery.preprocessor.processor.freemarker.method;

import net.frju.androidquery.preprocessor.processor.data.DbField;
import net.frju.androidquery.preprocessor.processor.utils.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import freemarker.ext.beans.StringModel;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;

public class GetColumnSetterMethod implements TemplateMethodModelEx {

    private static final String GET_COLUMN_SETTER = "getColumnSetter";

    public static Map<String, Object> getMethodMap() {
        Map<String, Object> map = new HashMap<>();
        map.put(GET_COLUMN_SETTER, new GetColumnSetterMethod());
        return map;
    }

    private GetColumnSetterMethod() {
    }

    @Override
    public Object exec(List arguments) throws TemplateModelException {
        Object var = arguments.get(0);
        String varName = var instanceof SimpleScalar ?
                var.toString() :
                String.valueOf(var);

        var = arguments.get(1);
        String valueVarName = var instanceof SimpleScalar ?
                var.toString() :
                String.valueOf(var);

        Object columnValue = arguments.get(2);

        DbField dbField;
        if (columnValue instanceof StringModel) {
            StringModel stringModel = (StringModel) columnValue;
            dbField = (DbField) stringModel.getAdaptedObject(DbField.class);
        } else {
            throw new IllegalStateException("The assembleColumnSetter argument must be type of " +
                    "net.frju.androidquery.preprocessor.processor.data.DbField");
        }

        return StringUtils.getSetter(varName, valueVarName, dbField);
    }
}
