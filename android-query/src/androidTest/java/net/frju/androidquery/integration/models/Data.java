package net.frju.androidquery.integration.models;

import net.frju.androidquery.annotation.Column;
import net.frju.androidquery.annotation.Table;

@Table(localDatabaseProvider = LocalDatabaseProvider.class)
public class Data {
    @Column(primaryKey = true, autoIncrement = true)
    public int id;
    @Column
    public String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
