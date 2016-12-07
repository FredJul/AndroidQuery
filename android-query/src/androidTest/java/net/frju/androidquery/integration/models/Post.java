package net.frju.androidquery.integration.models;

import net.frju.androidquery.annotation.Column;
import net.frju.androidquery.annotation.ForeignKey;
import net.frju.androidquery.annotation.Table;

@Table(
        foreignKeys = {
                @ForeignKey(
                        targetTable = "User",
                        targetColumn = "id",
                        localColumn = "userId"
                )
        },
        localDatabaseProvider = LocalDatabaseProvider.class
)
public class Post {
    @Column(index = true)
    public long id;
    @Column
    public String title;
    @Column
    public String body;
    @Column
    public long timestamp;
    @Column
    public long userId;
    @Column
    public User user;
    @Column
    public Data data;
}