package com.memtrip.sqlking.integration.utils;

import com.memtrip.sqlking.database.DatabaseProvider;
import com.memtrip.sqlking.integration.models.Log;
import com.memtrip.sqlking.operation.function.Delete;
import com.memtrip.sqlking.operation.function.Insert;

public class SetupLog {
    public static final int LOG_1_ID = 1;
    public static final long LOG_1_TIMESTAMP = 123456789;

    public void tearDownTestLogs(DatabaseProvider databaseProvider) {
        Delete.getBuilder().execute(Log.class, databaseProvider);
    }

    public void setupTestLogs(DatabaseProvider databaseProvider) {
        Log[] logs = {
                createLog(
                        LOG_1_ID,
                        LOG_1_TIMESTAMP
                ),
                createLog(
                        1700,
                        System.currentTimeMillis()
                )
        };

        Insert.getBuilder()
                .values(logs)
                .execute(databaseProvider);
    }

    public static Log createLog(int id, long timestamp) {
        Log log = new Log();
        log.setId(id);
        log.setTimestamp(timestamp);
        return log;
    }
}
