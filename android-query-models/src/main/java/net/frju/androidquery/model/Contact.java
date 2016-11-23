package net.frju.androidquery.model;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import net.frju.androidquery.annotation.Column;
import net.frju.androidquery.annotation.Table;
import net.frju.androidquery.models.gen.Q;
import net.frju.androidquery.operation.function.Count;
import net.frju.androidquery.operation.function.Delete;
import net.frju.androidquery.operation.function.Insert;
import net.frju.androidquery.operation.function.Result;
import net.frju.androidquery.operation.function.Save;
import net.frju.androidquery.operation.function.Select;
import net.frju.androidquery.operation.function.Update;

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
        return Q.Contact.deleteViaContentProvider();
    }

    public static Insert.Builder<Contact> insert(Contact... models) {
        return Q.Contact.insertViaContentProvider(models);
    }

    public static Update.Builder<Contact> update() {
        return Q.Contact.updateViaContentProvider();
    }

    public static Save.Builder<Contact> save(Contact... models) {
        return Q.Contact.saveViaContentProvider(models);
    }

    public static Result<Contact> fromCursor(Cursor cursor) {
        return Q.Contact.fromCursor(cursor);
    }
}
