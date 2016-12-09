package net.frju.androidquery.integration.models;

import net.frju.androidquery.annotation.Column;
import net.frju.androidquery.annotation.Table;

@Table(localDatabaseProvider = LocalDatabaseProvider.class)
public class ModelWithGettersSetters {
    @Column(primaryKey = true, autoIncrement = true, realName = "_id")
    private long id;
    @Column
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
