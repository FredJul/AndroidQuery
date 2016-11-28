package net.frju.androidquery.preprocessor.processor.freemarker.method;

import net.frju.androidquery.preprocessor.processor.data.Column;
import net.frju.androidquery.preprocessor.processor.data.Data;
import net.frju.androidquery.preprocessor.processor.data.Table;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import freemarker.ext.beans.StringModel;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;

public class IsPrimaryKeyAutoIncrementMethod implements TemplateMethodModelEx {

    private static final String IS_PRIMARY_KEY_AUTOINCREMENT = "isPrimaryKeyAutoIncrement";

    private Data mData;

    public static Map<String, Object> getMethodMap(Data data) {
        Map<String, Object> map = new HashMap<>();
        map.put(IS_PRIMARY_KEY_AUTOINCREMENT, new IsPrimaryKeyAutoIncrementMethod(data));
        return map;
    }

    private IsPrimaryKeyAutoIncrementMethod(Data data) {
        mData = data;
    }

    private String assembleIsPrimaryKeyAutoIncrement(Table table) {
        for (Column column : table.getColumns()) {
            if (column.hasPrimaryKey() && column.hasAutoIncrement()) {
                return "true";
            }
        }

        return "false";
    }

    @Override
    public Object exec(List arguments) throws TemplateModelException {
        Object tableNameValue = arguments.get(0);

        Table table;
        if (tableNameValue instanceof StringModel) {
            StringModel stringModel = (StringModel) tableNameValue;
            table = (Table) stringModel.getAdaptedObject(Table.class);
        } else {
            throw new IllegalStateException("The isPrimaryKeyAutoIncrement argument must be type of " +
                    "net.frju.androidquery.preprocessor.processor.data.Table");
        }

        return assembleIsPrimaryKeyAutoIncrement(table);
    }
}
