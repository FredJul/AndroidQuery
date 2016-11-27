package net.frju.androidquery.sample.model;

import net.frju.androidquery.annotation.Column;
import net.frju.androidquery.annotation.Table;
import net.frju.androidquery.sample.provider.ContentDatabaseProvider;
import net.frju.androidquery.sample.provider.LocalDatabaseProvider;

@Table(localDatabaseProvider = LocalDatabaseProvider.class, contentDatabaseProvider = ContentDatabaseProvider.class)
public class User {
    @Column(primaryKey = true, realName = "_id", autoIncrement = true)
    public int id;
    @Column
    public String username;
}
