package net.frju.androidquery.preprocessor.processor.data;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

public class DbModel {
    private Element mElement;
    private String mName;
    private String mRealName;
    private String mPackage;
    private String mType;
    private List<DbField> mDbFields;
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

    public String getPrimaryKeyName() {
        for (DbField dbField : mDbFields) {
            if (dbField.hasPrimaryKey()) {
                return dbField.getName();
            }
        }
        return "";
    }

    public String getPrimaryKeyRealName() {
        for (DbField dbField : mDbFields) {
            if (dbField.hasPrimaryKey()) {
                return dbField.getRealName();
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
