package com.memtrip.sqlking.converter;

import com.memtrip.sqlking.common.BaseTypeConverter;

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
