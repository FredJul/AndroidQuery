package com.memtrip.sqlking.model;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import com.memtrip.sqlking.common.Column;
import com.memtrip.sqlking.common.Table;
import com.memtrip.sqlking.operation.function.Count;
import com.memtrip.sqlking.operation.function.Delete;
import com.memtrip.sqlking.operation.function.Insert;
import com.memtrip.sqlking.operation.function.Result;
import com.memtrip.sqlking.operation.function.Select;

import net.frju.androidquery.models.gen.Q;

@SuppressWarnings("unused")
@Table(realName = "contacts", contentDatabaseProvider = ContactContentDatabaseProvider.class)
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

    public static void init(Context context) {
        Q.init(context);
    }

    public static Count.Builder<Contact> count() {
        return Q.Contact.count();
    }

    public static Select.Builder<Contact> select() {
        return Q.Contact.select();
    }

    public static Delete.Builder<Contact> delete() {
        return Q.Contact.deleteWithContentProvider();
    }

    public static Insert.Builder<Contact> insert(Contact... models) {
        return Q.Contact.insertWithContentProvider(models);
    }

    public static Result<Contact> fromCursor(Cursor cursor) {
        return Q.Contact.fromCursor(cursor);
    }
}
