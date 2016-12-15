package net.frju.androidquery.preprocessor.processor.freemarker.method;

import net.frju.androidquery.preprocessor.processor.data.DbField;
import net.frju.androidquery.preprocessor.processor.data.DbModel;
import net.frju.androidquery.preprocessor.processor.utils.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import freemarker.template.SimpleScalar;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;

public class JoinReferencesMethod implements TemplateMethodModelEx {

    private static final String JOIN = "joinReferences";

    public static Map<String, Object> getMethodMap() {
        Map<String, Object> map = new HashMap<>();
        map.put(JOIN, new JoinReferencesMethod());
        return map;
    }

    private JoinReferencesMethod() {

    }

    private String build(String joinTableName, List<DbModel> dbModels) {
        StringBuilder sb = new StringBuilder();

        DbModel joinDbModel = getTableFromName(joinTableName, dbModels);

        if (joinDbModel != null) {
            List<DbField> dbFields = joinDbModel.getFields();
            for (DbField dbField : dbFields) {
                if (dbField.isJoinable(dbModels)) {
                    DbModel columnDbModel = dbField.getRootTable(dbModels);
                    sb.append(buildJoinTable(joinDbModel, dbField, columnDbModel));
                    sb.append(build(dbField.getClassName(), dbModels));
                }
            }
        }

        return sb.toString();
    }

    private DbModel getTableFromName(String tableName, List<DbModel> dbModels) {
        for (DbModel dbModel : dbModels) {
            if (dbModel.getName().toLowerCase().equals(tableName.toLowerCase())) {
                return dbModel;
            }
        }

        return null;
    }

    private String buildJoinTable(DbModel joinDbModel, DbField dbField, DbModel dbModel) {
        return dbModel.getPackage() +
                "." +
                dbModel.getName() +
                " " +
                dbModel.getName().toLowerCase() +
                " = new " +
                dbModel.getPackage() +
                "." +
                dbModel.getName() +
                "();" +
                System.getProperty("line.separator") +
                StringUtils.getSetter(joinDbModel.getName().toLowerCase(), dbModel.getName().toLowerCase(), dbField) +
                ";" +
                System.getProperty("line.separator") +
                System.getProperty("line.separator");
        }

    @Override
    public Object exec(List arguments) throws TemplateModelException {
        Object joinTableNameValue = arguments.get(0);
        Object tablesValue = arguments.get(1);

        String joinTableName = joinTableNameValue instanceof SimpleScalar ?
                joinTableNameValue.toString() :
                String.valueOf(joinTableNameValue);

        List<DbModel> dbModels = Util.getTables(tablesValue);

        return build(joinTableName, dbModels);
    }
}