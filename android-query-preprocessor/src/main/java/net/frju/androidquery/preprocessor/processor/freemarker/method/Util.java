package net.frju.androidquery.preprocessor.processor.freemarker.method;

import net.frju.androidquery.preprocessor.processor.data.DbModel;

import java.util.ArrayList;
import java.util.List;

import freemarker.ext.beans.StringModel;
import freemarker.template.SimpleSequence;
import freemarker.template.TemplateModelException;

class Util {

    static List<DbModel> getTables(Object simpleSequenceValue) throws TemplateModelException {
        List<DbModel> dbModels = new ArrayList<>();

        if (simpleSequenceValue instanceof SimpleSequence) {
            SimpleSequence simpleSequence = (SimpleSequence) simpleSequenceValue;

            for (int i = 0; i < simpleSequence.size(); i++) {
                StringModel templateModel = (StringModel) simpleSequence.get(i);
                DbModel dbModel = (DbModel) templateModel.getAdaptedObject(DbModel.class);
                dbModels.add(dbModel);
            }
        }

        return dbModels;
    }
}
