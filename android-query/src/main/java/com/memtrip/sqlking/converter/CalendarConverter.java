package com.memtrip.sqlking.converter;

import com.memtrip.sqlking.common.BaseTypeConverter;

import java.util.Calendar;

/**
 * Author: andrewgrosner
 * Description: Defines how we store and retrieve a {@link java.util.Calendar}
 */
public class CalendarConverter extends BaseTypeConverter<Long, Calendar> {

    @Override
    public Long convertToDb(Calendar model) {
        return model == null ? null : model.getTimeInMillis();
    }

    @Override
    public Calendar convertFromDb(Long data) {
        if (data != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(data);
            return calendar;
        } else {
            return null;
        }
    }
}
