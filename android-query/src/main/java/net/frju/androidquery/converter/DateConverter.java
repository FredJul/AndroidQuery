package net.frju.androidquery.converter;

import net.frju.androidquery.annotation.BaseTypeConverter;

import java.util.Date;

/**
 * Author: andrewgrosner
 * Description: Defines how we store and retrieve a {@link java.util.Date}
 */
public class DateConverter extends BaseTypeConverter<Long, Date> {

    @Override
    public Long convertToDb(Date model) {
        return model == null ? null : model.getTime();
    }

    @Override
    public Date convertFromDb(Long data) {
        return data == null ? null : new Date(data);
    }
}
