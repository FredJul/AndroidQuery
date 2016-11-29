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
    public int id; // no index supplied for the @Test testNoUserIndexesAreCreated()
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
    public int logId;
    @Column
    public Log log;
    @Column
    public String nullField;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String newVal) {
        username = newVal;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long newVal) {
        timestamp = newVal;
    }

    public boolean getIsRegistered() {
        return isRegistered;
    }

    public void setIsRegistered(boolean newVal) {
        isRegistered = newVal;
    }

    public byte[] getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(byte[] newVal) {
        profilePicture = newVal;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getLogId() {
        return logId;
    }

    public void setLogId(int logId) {
        this.logId = logId;
    }

    public Log getLog() {
        return log;
    }

    public void setLog(Log log) {
        this.log = log;
    }
}