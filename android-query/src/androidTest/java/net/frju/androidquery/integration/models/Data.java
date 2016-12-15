package net.frju.androidquery.integration.models;

import net.frju.androidquery.annotation.DbField;
import net.frju.androidquery.annotation.DbModel;

@DbModel(localDatabaseProvider = LocalDatabaseProvider.class)
public class Data {
    @DbField(primaryKey = true, autoIncrement = true)
    public long id;
    @DbField
    public String name;
}
