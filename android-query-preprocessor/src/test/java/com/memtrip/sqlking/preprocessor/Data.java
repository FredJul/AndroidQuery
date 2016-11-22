package net.frju.androidquery.preprocessor;

import net.frju.androidquery.annotation.Column;
import net.frju.androidquery.annotation.Table;

@Table
public class Data {
    @Column(primaryKey = true, autoIncrement = true)
    int id;
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
