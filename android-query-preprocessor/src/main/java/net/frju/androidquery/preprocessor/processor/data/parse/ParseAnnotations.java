package net.frju.androidquery.preprocessor.processor.data.parse;

import net.frju.androidquery.preprocessor.processor.data.Data;
import net.frju.androidquery.preprocessor.processor.data.DbModel;
import net.frju.androidquery.preprocessor.processor.data.TypeConverter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

public class ParseAnnotations {

    public static Data parse(Set<? extends Element> tableElements, Set<? extends Element> converterElements) {
        Data data = new Data();
        data.setDatabaseProviders(assembleDatabaseProviders(tableElements));
        data.setTables(assembleTables(tableElements));
        data.setConverters(assembleConverters(converterElements));
        return data;
    }

    private static Set<TypeMirror> assembleDatabaseProviders(Set<? extends Element> elements) {
        Set<TypeMirror> providersSet = new HashSet<>();

        for (Element element : elements) {
            if (element.getKind().isClass()) {
                providersSet.add(ParseModelAnnotation.assembleDatabaseProvider(element));
            }
        }

        return providersSet;
    }

    private static List<DbModel> assembleTables(Set<? extends Element> elements) {
        List<DbModel> dbModels = new ArrayList<>();

        for (Element element : elements) {
            if (element.getKind().isClass()) {
                dbModels.add(ParseModelAnnotation.parseModel(element));
            }
        }

        return dbModels;
    }

    private static List<TypeConverter> assembleConverters(Set<? extends Element> elements) {
        List<TypeConverter> converters = new ArrayList<>();

        TypeConverter defaultConverter = new TypeConverter();
        defaultConverter.setName("net.frju.androidquery.converter.CalendarConverter");
        defaultConverter.setDbClassName("java.lang.Long");
        defaultConverter.setModelClassName("java.util.Calendar");
        converters.add(defaultConverter);

        defaultConverter = new TypeConverter();
        defaultConverter.setName("net.frju.androidquery.converter.DateConverter");
        defaultConverter.setDbClassName("java.lang.Long");
        defaultConverter.setModelClassName("java.util.Date");
        converters.add(defaultConverter);

        defaultConverter = new TypeConverter();
        defaultConverter.setName("net.frju.androidquery.converter.JSONObjectConverter");
        defaultConverter.setDbClassName("java.lang.String");
        defaultConverter.setModelClassName("org.json.JSONObject");
        converters.add(defaultConverter);

        defaultConverter = new TypeConverter();
        defaultConverter.setName("net.frju.androidquery.converter.SqlDateConverter");
        defaultConverter.setDbClassName("java.lang.Long");
        defaultConverter.setModelClassName("java.sql.Date");
        converters.add(defaultConverter);

        defaultConverter = new TypeConverter();
        defaultConverter.setName("net.frju.androidquery.converter.UriConverter");
        defaultConverter.setDbClassName("java.lang.String");
        defaultConverter.setModelClassName("android.net.Uri");
        converters.add(defaultConverter);

        defaultConverter = new TypeConverter();
        defaultConverter.setName("net.frju.androidquery.converter.UUIDConverter");
        defaultConverter.setDbClassName("java.lang.String");
        defaultConverter.setModelClassName("java.util.UUID");
        converters.add(defaultConverter);

        defaultConverter = new TypeConverter();
        defaultConverter.setName("net.frju.androidquery.converter.BitSetConverter");
        defaultConverter.setDbClassName("byte[]");
        defaultConverter.setModelClassName("java.util.BitSet");
        converters.add(defaultConverter);

        for (Element element : elements) {
            if (element.getKind().isClass()) {
                converters.add(ParseTypeConverterAnnotation.parseTypeConverter(element));
            }
        }

        return converters;
    }
}
