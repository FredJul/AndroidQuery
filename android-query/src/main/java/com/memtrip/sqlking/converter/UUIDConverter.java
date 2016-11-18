package com.memtrip.sqlking.converter;

import com.memtrip.sqlking.common.BaseTypeConverter;

import java.util.UUID;

/**
 * Description: Responsible for converting a {@link UUID} to a {@link String}.
 *
 * @author Andrew Grosner (fuzz)
 */
public class UUIDConverter extends BaseTypeConverter<String, UUID> {

    @Override
    public String convertToDb(UUID model) {
        return model != null ? model.toString() : null;
    }

    @Override
    public UUID convertFromDb(String data) {
        return data == null ? null : UUID.fromString(data);
    }
}