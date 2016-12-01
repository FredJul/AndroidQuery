package net.frju.androidquery.preprocessor.processor.freemarker;

import net.frju.androidquery.preprocessor.processor.data.Data;
import net.frju.androidquery.preprocessor.processor.freemarker.method.AssembleCreateTableMethod;
import net.frju.androidquery.preprocessor.processor.freemarker.method.FormatConstantMethod;
import net.frju.androidquery.preprocessor.processor.freemarker.method.GetContentValueMethod;
import net.frju.androidquery.preprocessor.processor.freemarker.method.GetCursorGetterMethod;
import net.frju.androidquery.preprocessor.processor.freemarker.method.GetPrimaryKeyValueMethod;
import net.frju.androidquery.preprocessor.processor.freemarker.method.IsPrimaryKeyAutoIncrementMethod;
import net.frju.androidquery.preprocessor.processor.freemarker.method.JoinReferencesMethod;
import net.frju.androidquery.preprocessor.processor.freemarker.method.JoinSettersMethod;

import java.util.HashMap;
import java.util.Map;

public class DataModel {

    private static final String PACKAGE_NAME = "package_name";
    private static final String TABLES = "tables";

    public static Map<String, Object> create(String packageName, Data data) {
        Map<String, Object> map = new HashMap<>();

        map.put(PACKAGE_NAME, packageName);
        map.put(TABLES, data.getTables());
        map.putAll(GetCursorGetterMethod.getMethodMap(data));
        map.putAll(GetContentValueMethod.getMethodMap(data));
        map.putAll(GetPrimaryKeyValueMethod.getMethodMap(data));
        map.putAll(IsPrimaryKeyAutoIncrementMethod.getMethodMap());
        map.putAll(AssembleCreateTableMethod.getMethodMap(data));
        map.putAll(FormatConstantMethod.getMethodMap());
        map.putAll(JoinSettersMethod.getMethodMap(data));
        map.putAll(JoinReferencesMethod.getMethodMap());

        return map;
    }
}
