package net.frju.androidquery.preprocessor.processor.data;


import java.util.List;
import java.util.Set;

import javax.lang.model.type.TypeMirror;

public class Data {
    private Set<TypeMirror> mDatabaseProviders;
    private List<DbModel> mDbModels;

    private List<TypeConverter> mConverters;

    public Set<TypeMirror> getDatabaseProviders() {
        return mDatabaseProviders;
    }

    public void setDatabaseProviders(Set<TypeMirror> newVal) {
        mDatabaseProviders = newVal;
    }

    public List<DbModel> getTables() {
        return mDbModels;
    }

    public void setTables(List<DbModel> newVal) {
        mDbModels = newVal;
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