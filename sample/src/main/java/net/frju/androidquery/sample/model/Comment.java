package net.frju.androidquery.sample.model;

import net.frju.androidquery.annotation.Column;
import net.frju.androidquery.annotation.Table;
import net.frju.androidquery.sample.provider.ContentDatabaseProvider;
import net.frju.androidquery.sample.provider.LocalDatabaseProvider;

@Table(localDatabaseProvider = LocalDatabaseProvider.class, contentDatabaseProvider = ContentDatabaseProvider.class)
public class Comment {
    @Column(index = true, realName = "_id")
    public int id;
    @Column
    public String body;
    @Column
    public long timestamp;
    @Column
    public int userId;
    @Column
    public User user;
}
