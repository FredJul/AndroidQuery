package com.memtrip.sqlking.sample.model;

import com.memtrip.sqlking.common.Column;
import com.memtrip.sqlking.common.Table;

@Table
public class User {
    @Column(index = true)
    int _id;
    @Column
    String username;

    public int get_id() {
        return _id;
    }

    public void set_id(int id) {
        this._id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
