package com.memtrip.sqlking.preprocessor;

import com.memtrip.sqlking.common.Column;
import com.memtrip.sqlking.common.Table;
import com.memtrip.sqlking.common.Index;
import com.memtrip.sqlking.common.IndexColumn;
import com.memtrip.sqlking.common.ForeignKey;

@Table
public class Data {
    @Column(primary_key = true, auto_increment = true) int id;
    @Column String name;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
