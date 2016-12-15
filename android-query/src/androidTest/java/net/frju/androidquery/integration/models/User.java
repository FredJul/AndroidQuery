package net.frju.androidquery.integration.models;

import net.frju.androidquery.annotation.DbField;
import net.frju.androidquery.annotation.DbModel;
import net.frju.androidquery.annotation.ForeignKey;

@DbModel(
        foreignKeys = {
                @ForeignKey(
                        targetTable = "Log",
                        targetColumn = "id",
                        localColumn = "logId"
                )
        },
        localDatabaseProvider = LocalDatabaseProvider.class,
        realName = "users"
)
public class User {
    @DbField(primaryKey = true, realName = "_id")
    public long id; // no index supplied for the @Test testNoUserIndexesAreCreated()
    @DbField
    public String username;
    @DbField
    public long timestamp;
    @DbField
    public boolean isRegistered;
    @DbField
    public byte[] profilePicture;
    @DbField
    public double rating;
    @DbField
    public int count;
    @DbField
    public long logId;
    @DbField
    public Log log;
    @DbField
    public String nullField;
}