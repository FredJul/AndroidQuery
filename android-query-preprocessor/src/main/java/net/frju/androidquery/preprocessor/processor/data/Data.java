package net.frju.androidquery.preprocessor.processor.data;


import java.util.List;

public class Data {
    private List<DbModel> mDbModels;

    private List<TypeConverter> mConverters;


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