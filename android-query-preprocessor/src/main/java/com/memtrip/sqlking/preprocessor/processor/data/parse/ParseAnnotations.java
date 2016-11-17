package com.memtrip.sqlking.preprocessor.processor.data.parse;

import com.memtrip.sqlking.preprocessor.processor.data.Data;
import com.memtrip.sqlking.preprocessor.processor.data.Table;
import com.memtrip.sqlking.preprocessor.processor.data.TypeConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.Element;

public class ParseAnnotations {

    public static Data parse(Set<? extends Element> tableElements, Set<? extends Element> converterElements) {
        Data data = new Data();
        data.setTables(assembleTables(tableElements));
        data.setConverters(assembleConverters(converterElements));
        return data;
    }

    private static List<Table> assembleTables(Set<? extends Element> elements) {
        List<Table> tables = new ArrayList<>();

        for (Element element : elements) {
            if (element.getKind().isClass()) {
                tables.add(ParseTableAnnotation.parseTable(element));
            }
        }

        return tables;
    }

    private static List<TypeConverter> assembleConverters(Set<? extends Element> elements) {
        List<TypeConverter> converters = new ArrayList<>();

        TypeConverter defaultConverter = new TypeConverter();
        defaultConverter.setName("com.memtrip.sqlking.converter.CalendarConverter");
        defaultConverter.setDbClassName("java.lang.Long");
        defaultConverter.setModelClassName("java.util.Calendar");
        converters.add(defaultConverter);

        defaultConverter = new TypeConverter();
        defaultConverter.setName("com.memtrip.sqlking.converter.DateConverter");
        defaultConverter.setDbClassName("java.lang.Long");
        defaultConverter.setModelClassName("java.util.Date");
        converters.add(defaultConverter);

        defaultConverter = new TypeConverter();
        defaultConverter.setName("com.memtrip.sqlking.converter.JSONObjectConverter");
        defaultConverter.setDbClassName("java.lang.String");
        defaultConverter.setModelClassName("org.json.JSONObject");
        converters.add(defaultConverter);

        defaultConverter = new TypeConverter();
        defaultConverter.setName("com.memtrip.sqlking.converter.SqlDateConverter");
        defaultConverter.setDbClassName("java.lang.Long");
        defaultConverter.setModelClassName("java.sql.Date");
        converters.add(defaultConverter);

        defaultConverter = new TypeConverter();
        defaultConverter.setName("com.memtrip.sqlking.converter.UriConverter");
        defaultConverter.setDbClassName("java.lang.String");
        defaultConverter.setModelClassName("android.net.Uri");
        converters.add(defaultConverter);

        defaultConverter = new TypeConverter();
        defaultConverter.setName("com.memtrip.sqlking.converter.UUIDConverter");
        defaultConverter.setDbClassName("java.lang.String");
        defaultConverter.setModelClassName("java.util.UUID");
        converters.add(defaultConverter);

        for (Element element : elements) {
            if (element.getKind().isClass()) {
                converters.add(ParseTypeConverterAnnotation.parseTypeConverter(element));
            }
        }

        return converters;
    }
}
