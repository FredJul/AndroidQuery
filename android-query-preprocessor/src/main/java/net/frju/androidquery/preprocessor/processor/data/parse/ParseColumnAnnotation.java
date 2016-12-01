package net.frju.androidquery.preprocessor.processor.data.parse;

import net.frju.androidquery.preprocessor.processor.data.Column;

import javax.lang.model.element.Element;
import javax.lang.model.element.Name;
import javax.lang.model.type.TypeMirror;

class ParseColumnAnnotation {

    static Column parseColumn(Element element) {

        String type = assembleType(element);

        Column column = new Column();
        column.setName(assembleName(element));
        column.setRealName(assembleRealName(element));
        column.setIsIndex(assembleIsIndex(element));
        column.setIsUnique(assembleIsUnique(element));
        column.setHasPrimaryKey(assemblePrimaryKey(element));
        column.setHasAutoIncrement(assembleAutoIncrement(element));
        column.setType(type);
        column.setClassName(assembleClassName(type));

        return column;
    }

    private static String assembleName(Element element) {
        Name name = element.getSimpleName();
        return name.toString();
    }

    private static String assembleRealName(Element element) {
        net.frju.androidquery.annotation.Column column = element.getAnnotation(net.frju.androidquery.annotation.Column.class);
        return column.realName();
    }

    private static String assembleType(Element element) {
        TypeMirror typeMirror = element.asType();
        return typeMirror.toString();
    }

    private static String assembleClassName(String type) {
        String[] packageParts = type.split("\\.");
        return packageParts[packageParts.length - 1];
    }

    private static boolean assembleIsIndex(Element element) {
        net.frju.androidquery.annotation.Column column = element.getAnnotation(net.frju.androidquery.annotation.Column.class);
        return column != null && column.index();
    }

    private static boolean assembleIsUnique(Element element) {
        net.frju.androidquery.annotation.Column column = element.getAnnotation(net.frju.androidquery.annotation.Column.class);
        return column != null && column.unique();
    }

    private static boolean assemblePrimaryKey(Element element) {
        net.frju.androidquery.annotation.Column column = element.getAnnotation(net.frju.androidquery.annotation.Column.class);
        return column.primaryKey();
    }

    private static boolean assembleAutoIncrement(Element element) {
        net.frju.androidquery.annotation.Column column = element.getAnnotation(net.frju.androidquery.annotation.Column.class);
        return column.autoIncrement();
    }
}
