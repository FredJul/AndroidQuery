package com.memtrip.sqlking.converter;

import android.net.Uri;

import com.memtrip.sqlking.common.BaseTypeConverter;

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