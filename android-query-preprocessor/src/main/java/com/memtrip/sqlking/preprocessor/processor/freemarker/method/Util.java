package com.memtrip.sqlking.preprocessor.processor.freemarker.method;

import com.memtrip.sqlking.preprocessor.processor.data.Table;

import java.util.ArrayList;
import java.util.List;

import freemarker.ext.beans.StringModel;
import freemarker.template.SimpleSequence;
import freemarker.template.TemplateModelException;

class Util {

    static List<Table> getTables(Object simpleSequenceValue) throws TemplateModelException {
        List<Table> tables = new ArrayList<>();

        if (simpleSequenceValue instanceof SimpleSequence) {
            SimpleSequence simpleSequence = (SimpleSequence) simpleSequenceValue;

            for (int i = 0; i < simpleSequence.size(); i++) {
                StringModel templateModel = (StringModel) simpleSequence.get(i);
                Table table = (Table) templateModel.getAdaptedObject(Table.class);
                tables.add(table);
            }
        }

        return tables;
    }
}
