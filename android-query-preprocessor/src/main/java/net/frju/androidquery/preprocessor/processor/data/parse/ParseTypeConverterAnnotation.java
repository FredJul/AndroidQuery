package net.frju.androidquery.preprocessor.processor.data.parse;

import net.frju.androidquery.preprocessor.processor.Context;
import net.frju.androidquery.preprocessor.processor.data.TypeConverter;

import javax.lang.model.element.Element;
import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;

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
        net.frju.androidquery.annotation.TypeConverter tableAnnotation = element.getAnnotation(net.frju.androidquery.annotation.TypeConverter.class);
        TypeMirror type = null;
        try {
            tableAnnotation.dbClass();
        } catch (MirroredTypeException mte) {
            type = mte.getTypeMirror();
        }
        return type.toString();
    }

    private static String assembleModelClassName(Element element) {
        net.frju.androidquery.annotation.TypeConverter tableAnnotation = element.getAnnotation(net.frju.androidquery.annotation.TypeConverter.class);
        TypeMirror type = null;
        try {
            tableAnnotation.modelClass();
        } catch (MirroredTypeException mte) {
            type = mte.getTypeMirror();
        }
        return type.toString();
    }

    private static String assemblePackage(Element element) {
        PackageElement packageElement = Context.getInstance().getElementUtils().getPackageOf(element);
        Name name = packageElement.getQualifiedName();
        return name.toString();
    }
}