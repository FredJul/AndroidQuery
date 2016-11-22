package net.frju.androidquery.converter;

import net.frju.androidquery.annotation.BaseTypeConverter;

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