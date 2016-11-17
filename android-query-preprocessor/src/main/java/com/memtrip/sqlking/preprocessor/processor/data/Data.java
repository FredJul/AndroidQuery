package com.memtrip.sqlking.preprocessor.processor.data;


import java.util.List;

public class Data {
    private List<Table> mTables;

    private List<TypeConverter> mConverters;


    public List<Table> getTables() {
        return mTables;
    }

    public void setTables(List<Table> newVal) {
        mTables = newVal;
    }

    public List<TypeConverter> getConverters() {
        return mConverters;
    }

    public void setConverters(List<TypeConverter> newVal) {
        mConverters = newVal;
    }

    public TypeConverter getConverterFromClass(String type) {

        for (TypeConverter converter : mConverters) {
            if (converter.getModelClassName().equals(type)) {
                return converter;
            }
        }

        return null;
    }
}