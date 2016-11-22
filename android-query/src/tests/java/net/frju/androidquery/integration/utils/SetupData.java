package net.frju.androidquery.integration.utils;

import net.frju.androidquery.database.DatabaseProvider;
import net.frju.androidquery.integration.models.Data;
import net.frju.androidquery.integration.models.Log;
import net.frju.androidquery.operation.function.Delete;
import net.frju.androidquery.operation.function.Insert;

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
