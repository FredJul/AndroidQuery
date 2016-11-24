package net.frju.androidquery.integration.utils;

import net.frju.androidquery.gen.Q;
import net.frju.androidquery.integration.models.Data;

public class SetupData {

    public void tearDownTestData() {
        Q.Data.delete().query();
    }

    public void setupTestData() {
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

        Q.Data.insert(data).query();
    }

    public static Data createData(String name) {
        Data data = new Data();
        data.setName(name);
        return data;
    }
}
