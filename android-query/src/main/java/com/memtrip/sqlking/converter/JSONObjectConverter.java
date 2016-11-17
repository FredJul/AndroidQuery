package com.memtrip.sqlking.converter;

import com.memtrip.sqlking.common.BaseTypeConverter;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONObjectConverter extends BaseTypeConverter<String, JSONObject> {

    @Override
    public String convertToDb(JSONObject model) {
        return model == null ? null : model.toString();
    }

    @Override
    public JSONObject convertFromDb(String data) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(data);
        } catch (JSONException e) {
            // maybe log this?
        } finally {
            return jsonObject;
        }
    }
}