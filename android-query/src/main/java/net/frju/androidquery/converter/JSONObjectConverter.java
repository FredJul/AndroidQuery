package net.frju.androidquery.converter;

import net.frju.androidquery.annotation.BaseTypeConverter;

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
        }
        return jsonObject;
    }
}