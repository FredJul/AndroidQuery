package com.memtrip.sqlking.sample.model;

import com.memtrip.sqlking.common.Column;
import com.memtrip.sqlking.common.Table;

@Table
public class User {
    @Column(index = true, realName = "_id")
    public int id;
    @Column
    public String username;
}
