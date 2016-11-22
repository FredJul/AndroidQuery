package net.frju.androidquery.integration.utils;

import net.frju.androidquery.database.DatabaseProvider;
import net.frju.androidquery.integration.models.Log;
import net.frju.androidquery.operation.function.Delete;
import net.frju.androidquery.operation.function.Insert;

public class SetupLog {
    public static final int LOG_1_ID = 1;
    public static final long LOG_1_TIMESTAMP = 123456789;

    public void tearDownTestLogs(DatabaseProvider databaseProvider) {
        Delete.getBuilder().query(Log.class, databaseProvider);
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
