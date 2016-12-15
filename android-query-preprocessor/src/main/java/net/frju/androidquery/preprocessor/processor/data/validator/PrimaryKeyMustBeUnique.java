package net.frju.androidquery.preprocessor.processor.data.validator;

import net.frju.androidquery.preprocessor.processor.Validator;
import net.frju.androidquery.preprocessor.processor.ValidatorException;
import net.frju.androidquery.preprocessor.processor.data.Data;
import net.frju.androidquery.preprocessor.processor.data.DbField;
import net.frju.androidquery.preprocessor.processor.data.DbModel;

import java.util.List;

public class PrimaryKeyMustBeUnique implements Validator {

    private final Data mData;

    public PrimaryKeyMustBeUnique(Data data) {
        this.mData = data;
    }

    private boolean primaryKeyIsUniqueInColumns(List<DbField> dbFields) {
        int occurrences = 0;

        if (dbFields != null) {
            for (DbField dbField : dbFields) {
                if (dbField.hasPrimaryKey()) {
                    occurrences++;
                }
            }
        }

        return occurrences > 1;
    }

    @Override
    public void validate() throws ValidatorException {
        for (DbModel dbModel : mData.getTables()) {
            if (primaryKeyIsUniqueInColumns(dbModel.getFields())) {
                throw new ValidatorException(
                        dbModel.getElement(),
                        "[Duplicate primaryKey's found in @DbModel: `" + dbModel.getName()
                                + ", only specify one primaryKey DbField per dbModel]"
                );
            }
        }
    }
}