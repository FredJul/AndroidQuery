package net.frju.androidquery.integration.models;

import net.frju.androidquery.annotation.DbField;
import net.frju.androidquery.annotation.DbModel;
import net.frju.androidquery.annotation.ForeignKey;

@DbModel(
        foreignKeys = {
                @ForeignKey(
                        targetTable = "User",
                        targetColumn = "id",
                        localColumn = "userId"
                )
        },
        localDatabaseProvider = LocalDatabaseProvider.class
)
public class Post {
    @DbField(index = true)
    public long id;
    @DbField
    public String title;
    @DbField
    public String body;
    @DbField
    public long timestamp;
    @DbField
    public long userId;
    @DbField
    public User user;
    @DbField
    public Data data;
}