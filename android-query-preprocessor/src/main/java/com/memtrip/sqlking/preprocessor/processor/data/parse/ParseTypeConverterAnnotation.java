package com.memtrip.sqlking.preprocessor.processor.data.parse;

import com.memtrip.sqlking.preprocessor.processor.Context;
import com.memtrip.sqlking.preprocessor.processor.data.TypeConverter;

import javax.lang.model.element.Element;
import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;

class ParseTypeConverterAnnotation {

    static TypeConverter parseTypeConverter(Element element) {
        TypeConverter converter = new TypeConverter();
        converter.setName(assemblePackage(element) + "." + assembleName(element));
        converter.setDbClassName(assembleDbClassName(element));
        converter.setModelClassName(assembleModelClassName(element));

        return converter;
    }

    private static String assembleName(Element element) {
        Name name = element.getSimpleName();
        return name.toString();
    }

    private static String assembleDbClassName(Element element) {
        com.memtrip.sqlking.common.TypeConverter tableAnnotation = element.getAnnotation(com.memtrip.sqlking.common.TypeConverter.class);
        return tableAnnotation.dbClass().getName();
    }

    private static String assembleModelClassName(Element element) {
        com.memtrip.sqlking.common.TypeConverter tableAnnotation = element.getAnnotation(com.memtrip.sqlking.common.TypeConverter.class);
        return tableAnnotation.modelClass().getName();
    }

    private static String assemblePackage(Element element) {
        PackageElement packageElement = Context.getInstance().getElementUtils().getPackageOf(element);
        Name name = packageElement.getQualifiedName();
        return name.toString();
    }
}