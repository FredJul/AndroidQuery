package net.frju.androidquery.sample.model;

import net.frju.androidquery.annotation.DbField;
import net.frju.androidquery.annotation.DbModel;
import net.frju.androidquery.sample.provider.LocalDatabaseProvider;

@DbModel(databaseProvider = LocalDatabaseProvider.class)
public class Comment {
    @DbField(primaryKey = true, dbName = "_id", autoIncrement = true)
    public long id;
    @DbField
    public String body;
    @DbField
    public long timestamp;
    @DbField
    public int userId;
    @DbField
    public User user;
}
