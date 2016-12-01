package net.frju.androidquery.preprocessor.processor.data.validator;

import net.frju.androidquery.preprocessor.processor.Validator;
import net.frju.androidquery.preprocessor.processor.ValidatorException;
import net.frju.androidquery.preprocessor.processor.data.Column;
import net.frju.androidquery.preprocessor.processor.data.Data;
import net.frju.androidquery.preprocessor.processor.data.Table;

import java.util.List;

public class PrimaryKeyMustBeUnique implements Validator {

    private final Data mData;

    public PrimaryKeyMustBeUnique(Data data) {
        this.mData = data;
    }

    private boolean primaryKeyIsUniqueInColumns(List<Column> columns) {
        int occurrences = 0;

        if (columns != null) {
            for (Column column : columns) {
                if (column.hasPrimaryKey()) {
                    occurrences++;
                }
            }
        }

        return occurrences > 1;
    }

    @Override
    public void validate() throws ValidatorException {
        for (Table table : mData.getTables()) {
            if (primaryKeyIsUniqueInColumns(table.getColumns())) {
                throw new ValidatorException(
                        table.getElement(),
                        "[Duplicate primaryKey's found in @Table: `" + table.getName()
                                + ", only specify one primaryKey Column per table]"
                );
            }
        }
    }
}