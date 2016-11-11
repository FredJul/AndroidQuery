package com.memtrip.sqlking.integration.utils;

import com.memtrip.sqlking.database.DatabaseProvider;
import com.memtrip.sqlking.integration.models.Data;
import com.memtrip.sqlking.integration.models.Log;
import com.memtrip.sqlking.operation.function.Delete;
import com.memtrip.sqlking.operation.function.Insert;

public class SetupData {

    public void tearDownTestData(DatabaseProvider databaseProvider) {
        Delete.getBuilder().execute(Log.class, databaseProvider);
    }

    public void setupTestData(DatabaseProvider databaseProvider) {
        Data[] data = {
                createData(
                    "data1"
                ),
                createData(
                    "data2"
                ),
                createData(
                    "data3"
                ),
        };

        Insert.getBuilder()
                .values(data)
                .execute(databaseProvider);
    }

    public static Data createData(String name) {
        Data data = new Data();
        data.setName(name);
        return data;
    }
}
