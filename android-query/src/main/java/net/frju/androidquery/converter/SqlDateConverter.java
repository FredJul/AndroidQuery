package net.frju.androidquery.converter;

import net.frju.androidquery.annotation.BaseTypeConverter;

import java.sql.Date;

/**
 * Author: andrewgrosner
 * Description: Defines how we store and retrieve a {@link java.sql.Date}
 */
public class SqlDateConverter extends BaseTypeConverter<Long, Date> {

    @Override
    public Long convertToDb(Date model) {
        return model == null ? null : model.getTime();
    }

    @Override
    public Date convertFromDb(Long data) {
        return data == null ? null : new Date(data);
    }
}
