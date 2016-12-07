package net.frju.androidquery.integration.models;

import net.frju.androidquery.annotation.Column;
import net.frju.androidquery.annotation.Table;

@Table(localDatabaseProvider = LocalDatabaseProvider.class)
public class Data {
    @Column(primaryKey = true, autoIncrement = true)
    public long id;
    @Column
    public String name;
}
