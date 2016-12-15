package net.frju.androidquery.preprocessor.processor.freemarker.method;

import net.frju.androidquery.preprocessor.processor.data.Data;
import net.frju.androidquery.preprocessor.processor.data.DbField;
import net.frju.androidquery.preprocessor.processor.data.DbModel;
import net.frju.androidquery.preprocessor.processor.utils.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import freemarker.template.SimpleScalar;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;

public class JoinSettersMethod implements TemplateMethodModelEx {

    private static final String JOIN = "join";

    private final Data mData;

    public static Map<String, Object> getMethodMap(Data data) {
        Map<String, Object> map = new HashMap<>();
        map.put(JOIN, new JoinSettersMethod(data));
        return map;
    }

    private JoinSettersMethod(Data data) {
        mData = data;
    }

    private String build(String joinTableName, List<DbModel> dbModels) {
        StringBuilder sb = new StringBuilder();

        for (DbModel dbModel : dbModels) {
            if (dbModel.getName().toLowerCase().equals(joinTableName.toLowerCase())) {
                List<DbField> dbFields = dbModel.getFields();
                for (DbField dbField : dbFields) {
                    if (dbField.isJoinable(dbModels)) {
                        sb.append(build(dbField.getClassName(), dbModels));
                    } else {
                        sb.append("} else if (cursor.getColumnName(x).equals(\"")
                                .append(dbModel.getRealName())
                                .append("_")
                                .append(dbField.getName())
                                .append("\")) {")
                                .append(System.getProperty("line.separator"))
                                .append(StringUtils.getSetter(dbModel.getName().toLowerCase(), StringUtils.assembleTypeGetter(mData, dbField.getType()), dbField))
                                .append(";");
                    }

                    sb.append(System.getProperty("line.separator"));
                }

                break;
            }
        }

        return sb.toString();
    }

    @Override
    public Object exec(List arguments) throws TemplateModelException {
        Object joinTableNameValue = arguments.get(0);
        Object tablesValue = arguments.get(1);

        String joinTableName = joinTableNameValue instanceof SimpleScalar ?
                joinTableNameValue.toString() :
                String.valueOf(joinTableNameValue);

        List<DbModel> dbModels = Util.getTables(tablesValue);

        String join = build(joinTableName, dbModels);
        if (join.length() > 0) {
            // remove the trailing "}"
            join = join.substring(0,join.length()-1);
        }

        return join;
    }
}