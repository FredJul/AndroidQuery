package net.frju.androidquery.preprocessor.processor.data.validator;

import net.frju.androidquery.preprocessor.processor.Validator;
import net.frju.androidquery.preprocessor.processor.ValidatorException;
import net.frju.androidquery.preprocessor.processor.data.Data;
import net.frju.androidquery.preprocessor.processor.data.DbModel;

import java.util.List;

public class TableNamesMustBeUniqueValidator implements Validator {
    private final Data mData;

    public TableNamesMustBeUniqueValidator(Data data) {
        mData = data;
    }

    private DbModel findDuplicateTable(List<DbModel> dbModels) {
        for (DbModel dbModel : dbModels) {
            DbModel duplicateDbModel = getDuplicateTable(dbModel, dbModels);
            if (duplicateDbModel != null) {
                return duplicateDbModel;
            }
        }

        return null;
    }

    private DbModel getDuplicateTable(DbModel check, List<DbModel> dbModels) {
        int occurrences = 0;

        for (DbModel dbModel : dbModels) {
            if (check.getName().equals(dbModel.getName())) occurrences++;
            if (occurrences > 1) return dbModel;
        }

        return null;
    }

    @Override
    public void validate() throws ValidatorException {
        DbModel dbModel = findDuplicateTable(mData.getTables());
        if (dbModel != null) {
            throw new ValidatorException(dbModel.getElement(), "[The @DbModel: `" + dbModel.getName() + "` is duplicated, dbModel names must be unique]");
        }
    }
}
