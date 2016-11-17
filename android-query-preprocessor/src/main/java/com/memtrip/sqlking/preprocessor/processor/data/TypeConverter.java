package com.memtrip.sqlking.preprocessor.processor.data;

public class TypeConverter {

    private String mName;

    private String mDbClassName;

    private String mModelClassName;

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public String getDbClassName() {
        return mDbClassName;
    }

    public void setDbClassName(String dbClassName) {
        mDbClassName = dbClassName;
    }

    public String getModelClassName() {
        return mModelClassName;
    }

    public void setModelClassName(String modelClassName) {
        mModelClassName = modelClassName;
    }
}
