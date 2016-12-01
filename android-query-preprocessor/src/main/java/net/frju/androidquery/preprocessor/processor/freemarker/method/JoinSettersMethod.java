package net.frju.androidquery.preprocessor.processor.freemarker.method;

import net.frju.androidquery.preprocessor.processor.data.Column;
import net.frju.androidquery.preprocessor.processor.data.Data;
import net.frju.androidquery.preprocessor.processor.data.Table;
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

    private String build(String joinTableName, List<Table> tables) {
        StringBuilder sb = new StringBuilder();

        for (Table table : tables) {
            if (table.getName().toLowerCase().equals(joinTableName.toLowerCase())) {
                List<Column> columns = table.getColumns();
                for (Column column : columns) {
                    if (column.isJoinable(tables)) {
                        sb.append(build(column.getClassName(), tables));
                    } else {
                        sb.append("} else if (cursor.getColumnName(x).equals(\"")
                                .append(table.getName())
                                .append("_")
                                .append(column.getName())
                                .append("\")) {")
                                .append(System.getProperty("line.separator"))
                                .append(table.getName().toLowerCase())
                                .append(".")
                                .append(column.getName())
                                .append(" = ")
                                .append(StringUtils.assembleTypeGetter(mData, column.getType()))
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

        List<Table> tables = Util.getTables(tablesValue);

        String join = build(joinTableName, tables);
        if (join.length() > 0) {
            // remove the trailing "}"
            join = join.substring(0,join.length()-1);
        }

        return join;
    }
}