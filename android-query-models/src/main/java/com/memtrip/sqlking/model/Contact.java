package com.memtrip.sqlking.model;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import com.memtrip.sqlking.common.Column;
import com.memtrip.sqlking.common.Table;
import com.memtrip.sqlking.database.ContentDatabaseProvider;
import com.memtrip.sqlking.model.gen.Q;
import com.memtrip.sqlking.operation.function.Result;

@SuppressWarnings("unused")
@Table(realName = "contacts")
public class Contact {

    @Column(realName = ContactsContract.Contacts._ID)
    public int id;

    @Column(realName = ContactsContract.Contacts.DISPLAY_NAME)
    public String displayName;

    @Column(realName = ContactsContract.Contacts.PHOTO_URI)
    public Uri photoUri;

    @Column(realName = ContactsContract.Contacts.PHOTO_THUMBNAIL_URI)
    public Uri photoThumbnailUri;

    @Column(realName = ContactsContract.Contacts.STARRED)
    public boolean starred;

    @Column(realName = ContactsContract.Contacts.CUSTOM_RINGTONE)
    public Uri customRingtone;

    @Column(realName = ContactsContract.Contacts.LOOKUP_KEY)
    public Uri lookupKey;

    public static ContentDatabaseProvider getContentDatabaseProvider(ContentResolver contentResolver) {
        return new ContentDatabaseProvider(contentResolver, ContactsContract.AUTHORITY, new Q.DefaultResolver());
    }

    public static Result<Contact> fromCursor(Cursor cursor) {
        return new Result<>(Contact.class, new Q.DefaultResolver(), cursor);
    }
}
