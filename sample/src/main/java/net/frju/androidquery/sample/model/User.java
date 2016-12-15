package net.frju.androidquery.sample.model;

import net.frju.androidquery.annotation.DbField;
import net.frju.androidquery.annotation.DbModel;
import net.frju.androidquery.sample.provider.ContentDatabaseProvider;
import net.frju.androidquery.sample.provider.LocalDatabaseProvider;

@DbModel(localDatabaseProvider = LocalDatabaseProvider.class, contentDatabaseProvider = ContentDatabaseProvider.class)
public class User {
    @DbField(primaryKey = true, realName = "_id", autoIncrement = true)
    public long id;
    @DbField
    public String username;
}
