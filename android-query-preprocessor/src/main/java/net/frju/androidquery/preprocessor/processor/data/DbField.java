package net.frju.androidquery.preprocessor.processor.data;

import java.util.List;

public class DbField {
    private String mName;
    private String mRealName;
    private String mClassName;
    private String mType;
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

    public String getRealName() {
        return mRealName.length() != 0 ? mRealName : mName;
    }

    public void setRealName(String newVal) {
        mRealName = newVal;
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