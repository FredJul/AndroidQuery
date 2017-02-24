package net.frju.androidquery.preprocessor.processor.freemarker.method;

import net.frju.androidquery.preprocessor.processor.data.Data;
import net.frju.androidquery.preprocessor.processor.data.DbField;
import net.frju.androidquery.preprocessor.processor.data.DbModel;
import net.frju.androidquery.preprocessor.processor.data.ForeignKey;
import net.frju.androidquery.preprocessor.processor.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import freemarker.ext.beans.StringModel;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;

public class AssembleCreateTableMethod implements TemplateMethodModelEx {

    private static final String ASSEMBLE_CREATE_TABLE = "assembleCreateTable";

    private final Data mData;

    public static Map<String, Object> getMethodMap(Data data) {
        Map<String, Object> map = new HashMap<>();
        map.put(ASSEMBLE_CREATE_TABLE, new AssembleCreateTableMethod(data));
        return map;
    }

    private AssembleCreateTableMethod(Data data) {
        mData = data;
    }

    /**
     * Build a create dbModel statement based on the provided tableName and members
     *
     * @param dbModel The dbModel that the statement will create
     * @return A SQL statement that will create a dbModel
     */
    private String buildCreateTableStatement(DbModel dbModel, List<DbModel> dbModels) {
        StringBuilder statementBuilder = new StringBuilder();

        statementBuilder.append("CREATE TABLE ");
        statementBuilder.append(dbModel.getDbName());
        statementBuilder.append(" (");

        HashMap<Integer, ArrayList<String>> uniqueGroups = new HashMap<>();

        for (int i = 0; i < dbModel.getFields().size(); i++) {
            DbField dbField = dbModel.getFields().get(i);

            String columnSql = StringUtils.columnToSql(mData, dbModels, dbField);
            if (columnSql != null) {
                statementBuilder.append(columnSql);
                statementBuilder.append(",");
            }

            if (dbField.uniqueGroup() != -1) {
                if (uniqueGroups.containsKey(dbField.uniqueGroup())) {
                    uniqueGroups.get(dbField.uniqueGroup()).add(dbField.getDbName());
                } else {
                    ArrayList<String> list = new ArrayList<>();
                    list.add(dbField.getDbName());
                    uniqueGroups.put(dbField.uniqueGroup(), list);
                }
            }
        }

        for (int uniqueGroup : uniqueGroups.keySet()) {
            statementBuilder.append("UNIQUE(")
                    .append(StringUtils.join(uniqueGroups.get(uniqueGroup), ","))
                    .append("),");
        }

        for (ForeignKey foreignKey : dbModel.getForeignKeys()) {
            statementBuilder.append("FOREIGN KEY(")
                    .append(foreignKey.getThisColumn()).append(") REFERENCES ")
                    .append(foreignKey.getTable())
                    .append("(")
                    .append(foreignKey.getForeignColumn())
                    .append("),");
        }

        if (statementBuilder.charAt(statementBuilder.length() - 1) == ',') {
            statementBuilder.deleteCharAt(statementBuilder.length() - 1);
        }

        statementBuilder.append(");");

        return "\"" + statementBuilder.toString() + "\";";
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

        return buildCreateTableStatement(dbModel, dbModels);
    }
}