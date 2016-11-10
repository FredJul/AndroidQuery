package com.memtrip.sqlking.sample.model;

import android.content.ContentResolver;
import android.provider.ContactsContract;

import com.memtrip.sqlking.common.Column;
import com.memtrip.sqlking.common.Table;
import com.memtrip.sqlking.database.ContentDatabaseProvider;
import com.memtrip.sqlking.database.Resolver;

@Table
public class Contacts {

    @Column
    public int _id;

    @Column
    public String display_name;

    public static ContentDatabaseProvider getContentDatabaseProvider(ContentResolver contentResolver, Resolver resolver) {
        return new ContentDatabaseProvider(contentResolver, ContactsContract.AUTHORITY, resolver);
    }
}
