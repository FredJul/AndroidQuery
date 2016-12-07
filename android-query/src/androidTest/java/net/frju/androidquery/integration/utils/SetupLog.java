package net.frju.androidquery.integration.utils;

import net.frju.androidquery.gen.Q;
import net.frju.androidquery.integration.models.Log;

public class SetupLog {
    public static final int LOG_1_ID = 1;
    public static final long LOG_1_TIMESTAMP = 123456789;

    public void tearDownTestLogs() {
        Q.Log.delete().query();
    }

    public void setupTestLogs() {
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

        Q.Log.insert(logs).query();
    }

    public static Log createLog(int id, long timestamp) {
        Log log = new Log();
        log.id = id;
        log.timestamp = timestamp;
        return log;
    }
}
