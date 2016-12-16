package net.frju.androidquery.sample.model;

import net.frju.androidquery.annotation.DbField;
import net.frju.androidquery.annotation.DbModel;
import net.frju.androidquery.sample.provider.LocalDatabaseProvider;

@DbModel(databaseProvider = LocalDatabaseProvider.class)
public class User {
    @DbField(primaryKey = true, dbName = "_id", autoIncrement = true)
    public long id;
    @DbField
    public String username;
}
