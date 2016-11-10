package com.memtrip.sqlking.sample.model;

import com.memtrip.sqlking.common.Column;
import com.memtrip.sqlking.common.Table;

@Table
public class Comment {
    @Column(index = true)
    int _id;
    @Column
    String body;
    @Column
    long timestamp;
    @Column
    int userId;
    @Column
    User user;

    public int get_id() {
        return _id;
    }

    public void set_id(int id) {
        this._id = id;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
