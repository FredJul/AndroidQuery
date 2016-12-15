package net.frju.androidquery.integration.models;

import net.frju.androidquery.annotation.DbField;
import net.frju.androidquery.annotation.DbModel;

@DbModel(localDatabaseProvider = LocalDatabaseProvider.class)
public class Log {
    @DbField(primaryKey = true)
    public long id;
    @DbField
    public long timestamp;
}
