package com.memtrip.sqlking.sample.model;

import android.content.ContentResolver;
import android.provider.ContactsContract;

import com.memtrip.sqlking.common.Column;
import com.memtrip.sqlking.common.Table;
import com.memtrip.sqlking.database.ContentDatabaseProvider;
import com.memtrip.sqlking.database.Resolver;

@Table(realName = "contacts")
public class Contact {

    @Column(realName = ContactsContract.Contacts._ID)
    public int id;

    @Column(realName = ContactsContract.Contacts.DISPLAY_NAME)
    public String displayName;

    //TODO custom type support
    //@Column(realName = ContactsContract.Contacts.LOOKUP_KEY)
    //public Uri lookupKey;

    public static ContentDatabaseProvider getContentDatabaseProvider(ContentResolver contentResolver, Resolver resolver) {
        return new ContentDatabaseProvider(contentResolver, ContactsContract.AUTHORITY, resolver);
    }
}
