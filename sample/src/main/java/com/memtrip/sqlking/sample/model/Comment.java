package com.memtrip.sqlking.sample.model;

import com.memtrip.sqlking.common.Column;
import com.memtrip.sqlking.common.Table;

@Table
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
