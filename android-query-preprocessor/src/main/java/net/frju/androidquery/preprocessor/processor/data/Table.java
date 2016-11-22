package net.frju.androidquery.preprocessor.processor.data;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

public class Table {
    private Element mElement;
    private String mName;
    private String mRealName;
    private String mPackage;
    private String mType;
    private List<Column> mColumns;
    private List<ForeignKey> mForeignKeys;
    private TypeMirror mLocalDatabaseProvider;
    private TypeMirror mContentDatabaseProvider;

    public Element getElement() {
        return mElement;
    }

    public void setElement(Element newVal) {
        mElement = newVal;
    }

    public String getName() {
        return mName;
    }

    public void setName(String newVal) {
        mName = newVal;
    }

    public String getRealName() {
        return mRealName.length() != 0 ? mRealName : mName;
    }

    public void setRealName(String newVal) {
        mRealName = newVal;
    }

    public String getPackage() {
        return mPackage;
    }

    public void setPackage(String newVal) {
        mPackage = newVal;
    }

    /**
     * (Used in Q.java freemarker template)
     */
    public String getType() {
        return mType;
    }

    public void setType(String newVal) {
        mType = newVal;
    }

    public List<Column> getColumns() {
        return mColumns;
    }

    public void setColumns(List<Column> newVal) {
        mColumns = newVal;
    }

    public String getPrimaryKeyRealName() {
        for (Column column : mColumns) {
            if (column.hasPrimaryKey()) {
                return column.getRealName();
            }
        }
        return "";
    }

    public List<ForeignKey> getForeignKeys() {
        return mForeignKeys;
    }

    public void setForeignKeys(List<ForeignKey> newVal) {
        mForeignKeys = newVal;
    }

    public TypeMirror getLocalDatabaseProvider() {
        return mLocalDatabaseProvider;
    }

    public void setLocalDatabaseProvider(TypeMirror localDatabaseProvider) {
        mLocalDatabaseProvider = localDatabaseProvider;
    }

    public TypeMirror getContentDatabaseProvider() {
        return mContentDatabaseProvider;
    }

    public void setContentDatabaseProvider(TypeMirror contentDatabaseProvider) {
        mContentDatabaseProvider = contentDatabaseProvider;
    }

    /**
     * (Used in Q.java freemarker template)
     * @return  all columns ignoring any object mappings
     */
    public List<Column> getMutableColumns(List<Table> tables) {
        List<Column> withoutObjects = new ArrayList<>();

        for (Column column : mColumns) {
            if (!column.isJoinable(tables)) {
                withoutObjects.add(column);
            }
        }

        return withoutObjects;
    }
}
