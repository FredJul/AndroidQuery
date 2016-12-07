package net.frju.androidquery.integration.models;

import net.frju.androidquery.annotation.Column;
import net.frju.androidquery.annotation.Table;

@Table(localDatabaseProvider = LocalDatabaseProvider.class)
public class Log {
    @Column(primaryKey = true)
    public long id;
    @Column
    public long timestamp;
}
