package net.frju.androidquery.integration.models;

import net.frju.androidquery.annotation.Column;
import net.frju.androidquery.annotation.Table;

@Table
public class Log {
    @Column(primaryKey = true)
    int id;
    @Column
    long timestamp;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
