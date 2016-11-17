package com.memtrip.sqlking.preprocessor.processor.freemarker;

import com.memtrip.sqlking.preprocessor.processor.data.Data;
import com.memtrip.sqlking.preprocessor.processor.freemarker.method.AssembleCreateTableMethod;
import com.memtrip.sqlking.preprocessor.processor.freemarker.method.FormatConstantMethod;
import com.memtrip.sqlking.preprocessor.processor.freemarker.method.GetContentValueMethod;
import com.memtrip.sqlking.preprocessor.processor.freemarker.method.GetCursorGetterMethod;
import com.memtrip.sqlking.preprocessor.processor.freemarker.method.JoinReferencesMethod;
import com.memtrip.sqlking.preprocessor.processor.freemarker.method.JoinSettersMethod;

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
        map.putAll(AssembleCreateTableMethod.getMethodMap(data));
        map.putAll(FormatConstantMethod.getMethodMap());
        map.putAll(JoinSettersMethod.getMethodMap(data));
        map.putAll(JoinReferencesMethod.getMethodMap());

        return map;
    }
}
