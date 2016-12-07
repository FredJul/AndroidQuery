package net.frju.androidquery.preprocessor.processor.data.parse;

import net.frju.androidquery.preprocessor.processor.Context;
import net.frju.androidquery.preprocessor.processor.data.Column;
import net.frju.androidquery.preprocessor.processor.data.ForeignKey;
import net.frju.androidquery.preprocessor.processor.data.Table;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;

import static net.frju.androidquery.preprocessor.processor.data.parse.ParseColumnAnnotation.parseColumn;

class ParseTableAnnotation {

    static Table parseTable(Element element) {

        String name = assembleName(element);
        String tablePackage = assemblePackage(element);

        Table table = new Table();
        table.setElement(element);
        table.setName(name);
        table.setRealName(assembleRealName(element));
        table.setPackage(tablePackage);
        table.setType(tablePackage + "." + name);
        table.setColumns(assembleColumns(element));
        table.setForeignKeys(assembleForeignKeys(element));
        table.setLocalDatabaseProvider(assembleLocalDatabaseProvider(element));
        table.setContentDatabaseProvider(assembleContentDatabaseProvider(element));

        return table;
    }

    private static String assembleName(Element element) {
        Name name = element.getSimpleName();
        return name.toString();
    }

    private static TypeMirror assembleLocalDatabaseProvider(Element element) {
        net.frju.androidquery.annotation.Table tableAnnotation = element.getAnnotation(net.frju.androidquery.annotation.Table.class);
        TypeMirror type = null;
        try {
            tableAnnotation.localDatabaseProvider();
        } catch (MirroredTypeException mte) {
            type = mte.getTypeMirror();
        }
        return type;
    }

    private static TypeMirror assembleContentDatabaseProvider(Element element) {
        net.frju.androidquery.annotation.Table tableAnnotation = element.getAnnotation(net.frju.androidquery.annotation.Table.class);
        TypeMirror type = null;
        try {
            tableAnnotation.contentDatabaseProvider();
        } catch (MirroredTypeException mte) {
            type = mte.getTypeMirror();
        }
        return type;
    }

    private static String assembleRealName(Element element) {
        net.frju.androidquery.annotation.Table tableAnnotation = element.getAnnotation(net.frju.androidquery.annotation.Table.class);
        return tableAnnotation.realName();
    }

    private static String assemblePackage(Element element) {
        PackageElement packageElement = Context.getInstance().getElementUtils().getPackageOf(element);
        Name name = packageElement.getQualifiedName();
        return name.toString();
    }

    private static List<Column> assembleColumns(Element element) {
        List<Column> columns = new ArrayList<>();

        for (Element childElement : Context.getInstance().getElementUtils().getAllMembers((TypeElement) element)) {
            if (childElement.getKind().isField() && childElement.getAnnotation(net.frju.androidquery.annotation.Column.class) != null) {
                columns.add(parseColumn(childElement));
            }
        }

        return columns;
    }

    private static List<ForeignKey> assembleForeignKeys(Element element) {
        net.frju.androidquery.annotation.Table tableAnnotation = element.getAnnotation(net.frju.androidquery.annotation.Table.class);
        net.frju.androidquery.annotation.ForeignKey[] foreignKeysAnnotation = tableAnnotation.foreignKeys();

        List<ForeignKey> foreignKeys = new ArrayList<>();

        for (net.frju.androidquery.annotation.ForeignKey annotation : foreignKeysAnnotation) {
            ForeignKey foreignKey = new ForeignKey();
            foreignKey.setTable(annotation.targetTable());
            foreignKey.setTargetColumn(annotation.targetColumn());
            foreignKey.setLocalColumn(annotation.localColumn());

            foreignKeys.add(foreignKey);
        }

        return foreignKeys;
    }
}