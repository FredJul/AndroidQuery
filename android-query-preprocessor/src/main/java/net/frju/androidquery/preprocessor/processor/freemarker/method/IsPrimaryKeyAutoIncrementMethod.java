package net.frju.androidquery.preprocessor.processor.freemarker.method;

import net.frju.androidquery.preprocessor.processor.data.DbField;
import net.frju.androidquery.preprocessor.processor.data.DbModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import freemarker.ext.beans.StringModel;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;

public class IsPrimaryKeyAutoIncrementMethod implements TemplateMethodModelEx {

    private static final String IS_PRIMARY_KEY_AUTOINCREMENT = "isPrimaryKeyAutoIncrement";

    public static Map<String, Object> getMethodMap() {
        Map<String, Object> map = new HashMap<>();
        map.put(IS_PRIMARY_KEY_AUTOINCREMENT, new IsPrimaryKeyAutoIncrementMethod());
        return map;
    }

    private IsPrimaryKeyAutoIncrementMethod() {
    }

    private String assembleIsPrimaryKeyAutoIncrement(DbModel dbModel) {
        for (DbField dbField : dbModel.getFields()) {
            if (dbField.hasPrimaryKey() && dbField.hasAutoIncrement()) {
                return "true";
            }
        }

        return "false";
    }

    @Override
    public Object exec(List arguments) throws TemplateModelException {
        Object tableNameValue = arguments.get(0);

        DbModel dbModel;
        if (tableNameValue instanceof StringModel) {
            StringModel stringModel = (StringModel) tableNameValue;
            dbModel = (DbModel) stringModel.getAdaptedObject(DbModel.class);
        } else {
            throw new IllegalStateException("The isPrimaryKeyAutoIncrement argument must be type of " +
                    "net.frju.androidquery.preprocessor.processor.data.DbModel");
        }

        return assembleIsPrimaryKeyAutoIncrement(dbModel);
    }
}
