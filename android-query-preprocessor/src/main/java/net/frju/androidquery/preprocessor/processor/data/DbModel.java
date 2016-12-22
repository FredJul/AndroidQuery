package net.frju.androidquery.preprocessor.processor.data;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

public class DbModel {
    private Element mElement;
    private String mName;
    private String mDbName;
    private String mPackage;
    private String mType;
    private List<DbField> mDbFields;
    private List<String> mInitMethodNames;
    private List<ForeignKey> mForeignKeys;
    private TypeMirror mDatabaseProvider;
    private boolean mHasLocalDatabaseProvider;

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

    public String getDbName() {
        return mDbName.length() != 0 ? mDbName : mName;
    }

    public void setDbName(String newVal) {
        mDbName = newVal;
    }

    public String getPackage() {
        return mPackage;
    }

    public void setPackage(String newVal) {
        mPackage = newVal;
    }

    public String getType() {
        return mType;
    }

    public void setType(String newVal) {
        mType = newVal;
    }

    public List<DbField> getFields() {
        return mDbFields;
    }

    public void setFields(List<DbField> newVal) {
        mDbFields = newVal;
    }

    public List<String> getInitMethodNames() {
        return mInitMethodNames;
    }

    public void setInitMethodNames(List<String> newVal) {
        mInitMethodNames = newVal;
    }

    public String getPrimaryKeyName() {
        for (DbField dbField : mDbFields) {
            if (dbField.hasPrimaryKey()) {
                return dbField.getName();
            }
        }
        return "";
    }

    public String getPrimaryKeyDbName() {
        for (DbField dbField : mDbFields) {
            if (dbField.hasPrimaryKey()) {
                return dbField.getDbName();
            }
        }
        return "";
    }

    public String getPrimaryKeyType() {
        for (DbField dbField : mDbFields) {
            if (dbField.hasPrimaryKey()) {
                return dbField.getType();
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

    public TypeMirror getDatabaseProvider() {
        return mDatabaseProvider;
    }

    public void setDatabaseProvider(TypeMirror databaseProvider) {
        mDatabaseProvider = databaseProvider;
    }

    public boolean hasLocalDatabaseProvider() {
        return mHasLocalDatabaseProvider;
    }

    public void setHasLocalDatabaseProvider(boolean newValue) {
        mHasLocalDatabaseProvider = newValue;
    }

    /**
     * (Used in Q.java freemarker template)
     * @param dbModels all dbModels
     * @return  all columns ignoring any object mappings
     */
    public List<DbField> getMutableFields(List<DbModel> dbModels) {
        List<DbField> withoutOtherModels = new ArrayList<>();

        for (DbField dbField : mDbFields) {
            if (!dbField.isJoinable(dbModels)) {
                withoutOtherModels.add(dbField);
            }
        }

        return withoutOtherModels;
    }
}
