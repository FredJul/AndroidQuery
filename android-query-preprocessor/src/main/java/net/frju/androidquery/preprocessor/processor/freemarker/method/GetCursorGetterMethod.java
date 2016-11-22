package net.frju.androidquery.preprocessor.processor.freemarker.method;

import net.frju.androidquery.preprocessor.processor.data.Data;
import net.frju.androidquery.preprocessor.processor.utils.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import freemarker.template.SimpleScalar;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;

public class GetCursorGetterMethod implements TemplateMethodModelEx {

    private static final String GET_COLUMN_NAMES = "getCursorGetter";

    private Data mData;

    public static Map<String, Object> getMethodMap(Data data) {
        Map<String, Object> map = new HashMap<>();
        map.put(GET_COLUMN_NAMES, new GetCursorGetterMethod(data));
        return map;
    }

    private GetCursorGetterMethod(Data data) {
        mData = data;
    }

    @Override
    public Object exec(List arguments) throws TemplateModelException {
        Object value = arguments.get(0);

        String typeValue = value instanceof SimpleScalar ?
                value.toString() :
                String.valueOf(value);

        return StringUtils.assembleTypeGetter(mData, typeValue);
    }
}