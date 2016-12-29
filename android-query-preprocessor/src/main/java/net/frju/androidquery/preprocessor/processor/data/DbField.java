package net.frju.androidquery.preprocessor.processor.data;

import java.util.List;

public class DbField {
    private String mName;
    private String mDbName;
    private String mClassName;
    private String mType;
    private String mGetterName;
    private String mSetterName;
    private boolean mIsIndex;
    private boolean mIsUnique;
    private boolean mPrimaryKey;
    private boolean mHasAutoIncrement;
    private boolean mIsPublicField;

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

    public String getClassName() {
        return mClassName;
    }

    public void setClassName(String newVal) {
        mClassName = newVal;
    }

    public String getType() {
        return mType;
    }

    public void setType(String newVal) {
        mType = newVal;
    }

    public String getGetterName() {
        return mGetterName;
    }

    public void setGetterName(String newVal) {
        mGetterName = newVal;
    }

    public String getSetterName() {
        return mSetterName;
    }

    public void setSetterName(String newVal) {
        mSetterName = newVal;
    }

    public boolean isIndex() {
        return mIsIndex;
    }

    public void setIsIndex(boolean newVal) {
        mIsIndex = newVal;
    }

    public boolean isUnique() {
        return mIsUnique;
    }

    public void setIsUnique(boolean newVal) {
        mIsUnique = newVal;
    }

    public boolean hasPrimaryKey() {
        return mPrimaryKey;
    }

    public void setHasPrimaryKey(boolean newVal) {
        mPrimaryKey = newVal;
    }

    public boolean hasAutoIncrement() {
        return mHasAutoIncrement;
    }

    public void setHasAutoIncrement(boolean newVal) {
        mHasAutoIncrement = newVal;
    }

    public boolean isIsPublicField() {
        return mIsPublicField;
    }

    public void setIsPublicField(boolean newVal) {
        mIsPublicField = newVal;
    }

    public DbModel getRootTable(List<DbModel> dbModels) {
        if (isJoinable(dbModels)) {
            for (DbModel dbModel : dbModels) {
                if (dbModel.getType().equals(mType)) {
                    return dbModel;
                }
            }
        }

        throw new IllegalStateException("Only joinable columns can call getRootTable");
    }

    public boolean isJoinable(List<DbModel> dbModels) {
        for (DbModel dbModel : dbModels) {
            if (dbModel.getName().toLowerCase().equals(mClassName.toLowerCase())) {
                return true;
            }
        }

        return false;
    }
}