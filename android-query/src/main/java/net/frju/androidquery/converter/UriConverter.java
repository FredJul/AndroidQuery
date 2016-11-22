package net.frju.androidquery.converter;

import android.net.Uri;

import net.frju.androidquery.annotation.BaseTypeConverter;

public class UriConverter extends BaseTypeConverter<String, Uri> {

    @Override
    public String convertToDb(Uri model) {
        return model == null ? null : model.toString();
    }

    @Override
    public Uri convertFromDb(String data) {
        return data == null ? null : Uri.parse(data);
    }
}