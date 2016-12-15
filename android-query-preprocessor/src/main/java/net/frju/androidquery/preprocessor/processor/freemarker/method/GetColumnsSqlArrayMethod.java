package net.frju.androidquery.preprocessor.processor.freemarker.method;

import net.frju.androidquery.preprocessor.processor.data.Data;
import net.frju.androidquery.preprocessor.processor.data.DbField;
import net.frju.androidquery.preprocessor.processor.data.DbModel;
import net.frju.androidquery.preprocessor.processor.utils.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import freemarker.ext.beans.StringModel;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;

public class GetColumnsSqlArrayMethod implements TemplateMethodModelEx {

    private static final String GET_COLUMNS_SQL_ARRAY = "getColumnsSqlArray";

    private final Data mData;

    public static Map<String, Object> getMethodMap(Data data) {
        Map<String, Object> map = new HashMap<>();
        map.put(GET_COLUMNS_SQL_ARRAY, new GetColumnsSqlArrayMethod(data));
        return map;
    }

    private GetColumnsSqlArrayMethod(Data data) {
        mData = data;
    }

    /**
     * Build a create dbModel statement based on the provided tableName and members
     *
     * @param    dbModel    The dbModel that the statement will create
     * @return A SQL statement that will create a dbModel
     */
    private String buildColumnSqlStringArray(DbModel dbModel, List<DbModel> dbModels) {
        StringBuilder statementBuilder = new StringBuilder();

        statementBuilder.append("new String[] {");

        for (int i = 0; i < dbModel.getFields().size(); i++) {
            DbField dbField = dbModel.getFields().get(i);

            String columnSql = StringUtils.columnToSql(mData, dbModels, dbField);
            if (columnSql != null) {
                statementBuilder.append("\"");
                statementBuilder.append(columnSql);
                statementBuilder.append("\",");
            }
        }

        if (statementBuilder.charAt(statementBuilder.length() - 1) == ',') {
            statementBuilder.deleteCharAt(statementBuilder.length() - 1);
        }

        statementBuilder.append("}");

        return statementBuilder.toString();
    }

    @Override
    public Object exec(List arguments) throws TemplateModelException {
        Object tableNameValue = arguments.get(0);
        Object tablesValue = arguments.get(1);

        DbModel dbModel;
        if (tableNameValue instanceof StringModel) {
            StringModel stringModel = (StringModel) tableNameValue;
            dbModel = (DbModel) stringModel.getAdaptedObject(DbModel.class);
        } else {
            throw new IllegalStateException("The assembleCreateTable argument must be type of " +
                    "net.frju.androidquery.preprocessor.processor.data.DbModel");
        }

        List<DbModel> dbModels = Util.getTables(tablesValue);

        return buildColumnSqlStringArray(dbModel, dbModels);
    }
}