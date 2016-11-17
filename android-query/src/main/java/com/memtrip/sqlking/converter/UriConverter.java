package com.memtrip.sqlking.converter;

import android.net.Uri;

import com.memtrip.sqlking.common.BaseTypeConverter;

public class UriConverter extends BaseTypeConverter<String, Uri> {

    @Override
    public String convertToDb(Uri model) {
        return model.toString();
    }

    @Override
    public Uri convertFromDb(String data) {
        return Uri.parse(data);
    }
}