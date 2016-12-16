package net.frju.androidquery.integration.models;

import net.frju.androidquery.annotation.DbField;
import net.frju.androidquery.annotation.DbModel;

@DbModel(databaseProvider = LocalDatabaseProvider.class)
public class ModelWithGettersSetters {
    @DbField(primaryKey = true, autoIncrement = true, dbName = "_id")
    private long id;
    @DbField
    protected String name;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
