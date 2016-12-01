package net.frju.androidquery.preprocessor.processor.freemarker.method;

import net.frju.androidquery.preprocessor.processor.data.Column;
import net.frju.androidquery.preprocessor.processor.data.Data;
import net.frju.androidquery.preprocessor.processor.data.Table;
import net.frju.androidquery.preprocessor.processor.data.TypeConverter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import freemarker.ext.beans.StringModel;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;

public class GetPrimaryKeyValueMethod implements TemplateMethodModelEx {

    private static final String GET_PRIMARY_KEY_VALUE = "getPrimaryKeyValue";

    private final Data mData;

    public static Map<String, Object> getMethodMap(Data data) {
        Map<String, Object> map = new HashMap<>();
        map.put(GET_PRIMARY_KEY_VALUE, new GetPrimaryKeyValueMethod(data));
        return map;
    }

    private GetPrimaryKeyValueMethod(Data data) {
        mData = data;
    }

    private String assemblePrimaryKeyValue(String varName, Table table) {
        for (Column column : table.getColumns()) {
            if (column.hasPrimaryKey()) {
                TypeConverter converter = mData.getConverterFromClass(column.getType());
                if (converter != null) {
                    return "new " + converter.getName() + "().convertToDb(" + varName + "." + column.getName() + ")";
                }

                String result = varName + "." + column.getName();
                switch (column.getType()) {
                    case "long":
                        result = "java.lang.Long.valueOf(" + result + ")";
                        break;
                    case "int":
                        result = "java.lang.Integer.valueOf(" + result + ")";
                        break;
                    case "short":
                        result = "java.lang.Short.valueOf(" + result + ")";
                        break;
                    case "double":
                        result = "java.lang.Double.valueOf(" + result + ")";
                        break;
                    case "float":
                        result = "java.lang.Float.valueOf(" + result + ")";
                        break;
                    case "java.lang.Long":
                    case "java.lang.Integer":
                    case "java.lang.Short":
                    case "java.lang.Double":
                    case "java.lang.Float":
                    case "java.lang.String":
                        break; // Nothing to do
                    default:
                        throw new IllegalStateException("PrimaryKey can not have the type: " + column.getType());
                }
                return result;
            }
        }

        return "null";
    }

    @Override
    public Object exec(List arguments) throws TemplateModelException {
        Object var = arguments.get(0);
        String varName = var instanceof SimpleScalar ?
                var.toString() :
                String.valueOf(var);

        Object tableNameValue = arguments.get(1);

        Table table;
        if (tableNameValue instanceof StringModel) {
            StringModel stringModel = (StringModel) tableNameValue;
            table = (Table) stringModel.getAdaptedObject(Table.class);
        } else {
            throw new IllegalStateException("The getPrimaryKeyValue argument must be type of " +
                    "net.frju.androidquery.preprocessor.processor.data.Table");
        }

        return assemblePrimaryKeyValue(varName, table);
    }
}
