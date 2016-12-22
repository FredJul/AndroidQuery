package net.frju.androidquery.preprocessor.processor.data.parse;

import net.frju.androidquery.preprocessor.processor.data.DbField;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.type.TypeMirror;

class ParseFieldAnnotation {

    static DbField parseField(Element element) {

        String type = assembleType(element);

        DbField dbField = new DbField();
        dbField.setName(assembleName(element));
        dbField.setDbName(assembleRealName(element));
        dbField.setIsIndex(assembleIsIndex(element));
        dbField.setIsUnique(assembleIsUnique(element));
        dbField.setHasPrimaryKey(assemblePrimaryKey(element));
        dbField.setHasAutoIncrement(assembleAutoIncrement(element));
        dbField.setType(type);
        dbField.setClassName(assembleClassName(type));
        dbField.setIsPublicField(assembleIsPublic(element));

        return dbField;
    }

    private static String assembleName(Element element) {
        Name name = element.getSimpleName();
        return name.toString();
    }

    private static String assembleRealName(Element element) {
        net.frju.androidquery.annotation.DbField dbField = element.getAnnotation(net.frju.androidquery.annotation.DbField.class);
        return dbField.dbName();
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
        net.frju.androidquery.annotation.DbField dbField = element.getAnnotation(net.frju.androidquery.annotation.DbField.class);
        return dbField != null && dbField.index();
    }

    private static boolean assembleIsUnique(Element element) {
        net.frju.androidquery.annotation.DbField dbField = element.getAnnotation(net.frju.androidquery.annotation.DbField.class);
        return dbField != null && dbField.unique();
    }

    private static boolean assemblePrimaryKey(Element element) {
        net.frju.androidquery.annotation.DbField dbField = element.getAnnotation(net.frju.androidquery.annotation.DbField.class);
        return dbField.primaryKey();
    }

    private static boolean assembleAutoIncrement(Element element) {
        net.frju.androidquery.annotation.DbField dbField = element.getAnnotation(net.frju.androidquery.annotation.DbField.class);
        return dbField.autoIncrement();
    }

    private static boolean assembleIsPublic(Element element) {
        return element.getModifiers().contains(Modifier.PUBLIC);
    }
}
