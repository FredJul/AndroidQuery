package net.frju.androidquery.integration.models;

import net.frju.androidquery.annotation.Column;
import net.frju.androidquery.annotation.ForeignKey;
import net.frju.androidquery.annotation.Table;

@Table(
        foreignKeys = {
                @ForeignKey(
                        targetTable = "Log",
                        targetColumn = "id",
                        localColumn = "logId"
                )
        },
        localDatabaseProvider = LocalDatabaseProvider.class
)
public class User {
    @Column(primaryKey = true)
    public long id; // no index supplied for the @Test testNoUserIndexesAreCreated()
    @Column
    public String username;
    @Column
    public long timestamp;
    @Column
    public boolean isRegistered;
    @Column
    public byte[] profilePicture;
    @Column
    public double rating;
    @Column
    public int count;
    @Column
    public long logId;
    @Column
    public Log log;
    @Column
    public String nullField;
}