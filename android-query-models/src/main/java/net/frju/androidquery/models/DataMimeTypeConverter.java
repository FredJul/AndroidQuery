package net.frju.androidquery.models;

import net.frju.androidquery.annotation.BaseTypeConverter;
import net.frju.androidquery.annotation.TypeConverter;

@TypeConverter(dbClass = String.class, modelClass = RawContactData.MimeType.class)
public class DataMimeTypeConverter extends BaseTypeConverter<String, RawContactData.MimeType> {

    @Override
    public String convertToDb(RawContactData.MimeType model) {
        return model == null ? null : model.toString();
    }

    @Override
    public RawContactData.MimeType convertFromDb(String data) {
        return RawContactData.MimeType.fromString(data);
    }
}