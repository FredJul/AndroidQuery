package net.frju.androidquery.models;

import android.net.Uri;
import android.provider.ContactsContract;

import net.frju.androidquery.annotation.DbField;
import net.frju.androidquery.annotation.DbModel;
import net.frju.androidquery.annotation.InitMethod;
import net.frju.androidquery.models.gen.Q;
import net.frju.androidquery.operation.condition.Condition;
import net.frju.androidquery.operation.condition.Where;

@SuppressWarnings("unused")
@DbModel(dbName = "contacts", databaseProvider = ContactContentDatabaseProvider.class)
public class Contact {

    /**
     * The unique ID for a row.
     */
    @DbField(primaryKey = true, autoIncrement = true, dbName = ContactsContract.Contacts._ID)
    public long id;

    /**
     * The display name for the contact.
     * <P>Type: TEXT</P>
     */
    @DbField(dbName = ContactsContract.Contacts.DISPLAY_NAME)
    public String displayName;

    /**
     * A URI that can be used to retrieve the contact's full-size photo.
     * If PHOTO_FILE_ID is not null, this will be populated with a URI based off
     * {@link ContactsContract.DisplayPhoto#CONTENT_URI}.  Otherwise, this will
     * be populated with the same value as {@link #photoThumbnailUri}.
     */
    @DbField(dbName = ContactsContract.Contacts.PHOTO_URI)
    public Uri photoUri;

    /**
     * A URI that can be used to retrieve a thumbnail of the contact's photo.
     * If the content provider does not differentiate between full-size photos
     * and thumbnail photos, {@link #photoThumbnailUri} and {@link #photoUri} can contain
     * the same value, but either both shall be null or both not null.
     */
    @DbField(dbName = ContactsContract.Contacts.PHOTO_THUMBNAIL_URI)
    public Uri photoThumbnailUri;

    /**
     * The number of times a contact has been contacted
     */
    @DbField(dbName = ContactsContract.Contacts.TIMES_CONTACTED)
    public int timesContacted;

    /**
     * The last time a contact was contacted.
     */
    @DbField(dbName = ContactsContract.Contacts.LAST_TIME_CONTACTED)
    public int lastTimeContacted;

    /**
     * Is the contact starred?
     */
    @DbField(dbName = ContactsContract.Contacts.STARRED)
    public boolean starred;

    /**
     * URI for a custom ringtone associated with the contact. If null or missing,
     * the default ringtone is used.
     */
    @DbField(dbName = ContactsContract.Contacts.CUSTOM_RINGTONE)
    public Uri customRingtone;

    /**
     * An opaque value that contains hints on how to find the contact if
     * its row id changed as a result of a sync or aggregation.
     */
    @DbField(dbName = ContactsContract.Contacts.LOOKUP_KEY)
    public Uri lookupKey;

    /**
     * Flag that reflects the {@link ContactsContract.Groups#GROUP_VISIBLE} state of any
     * {@link ContactsContract.CommonDataKinds.GroupMembership} for this contact.
     */
    @DbField(dbName = ContactsContract.Contacts.IN_VISIBLE_GROUP)
    public boolean inVisibleGroup;

    /**
     * An indicator of whether this contact has at least one phone number.
     */
    @DbField(dbName = ContactsContract.Contacts.HAS_PHONE_NUMBER)
    public boolean hasPhoneNumber;

    /**
     * The list of associated raw contacts. Only populated if queryAndInit()/rxAndInit() is called.
     */
    public RawContact[] rawContacts;

    @InitMethod
    public void initRawContacts() {
        rawContacts = Q.RawContact.select().where(Condition.where(Q.RawContact.CONTACT_ID, Where.Op.IS, id)).queryAndInit();
    }
}
