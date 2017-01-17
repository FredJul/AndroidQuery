package net.frju.androidquery.integration.models;

import net.frju.androidquery.annotation.DbField;
import net.frju.androidquery.annotation.DbModel;
import net.frju.androidquery.annotation.ForeignKey;
import net.frju.androidquery.annotation.InitMethod;
import net.frju.androidquery.gen.Q;
import net.frju.androidquery.operation.condition.Where;

@DbModel(
        foreignKeys = {
                @ForeignKey(
                        targetTable = "Log",
                        targetColumn = "id",
                        localColumn = "logId"
                )
        },
        databaseProvider = LocalDatabaseProvider.class,
        dbName = "users"
)
public class User {
    @DbField(primaryKey = true, dbName = "_id")
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

    public Post[] posts;

    @InitMethod
    public void initPosts() {
        posts = Q.POST.select().where(Where.field(Q.POST.USER_ID).isEqualTo(id)).queryAndInit();
    }
}