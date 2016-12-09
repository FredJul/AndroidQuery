package net.frju.androidquery.preprocessor.processor.data;

import java.util.List;

public class Column {
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

    /**
     * (Used in Q.java freemarker template)
     */
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

    public Table getRootTable(List<Table> tables) {
        if (isJoinable(tables)) {
            for (Table table : tables) {
                if (table.getType().equals(mType)) {
                    return table;
                }
            }
        }

        throw new IllegalStateException("Only joinable columns can call getRootTable");
    }

    public boolean isJoinable(List<Table> tables) {
        for (Table table : tables) {
            if (table.getName().toLowerCase().equals(mClassName.toLowerCase())) {
                return true;
            }
        }

        return false;
    }
}