package com.memtrip.sqlking.sample.model;

import com.memtrip.sqlking.common.Column;
import com.memtrip.sqlking.common.Table;

@Table
public class User {
    @Column(index = true)
    public int _id;
    @Column
    public String username;
}
