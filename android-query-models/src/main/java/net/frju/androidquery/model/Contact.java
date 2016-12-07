package net.frju.androidquery.model;

import android.net.Uri;
import android.provider.ContactsContract;

import net.frju.androidquery.annotation.Column;
import net.frju.androidquery.annotation.Table;

@SuppressWarnings("unused")
@Table(realName = "contacts", contentDatabaseProvider = ContactContentDatabaseProvider.class)
public class Contact {

    /**
     * The unique ID for a row.
     */
    @Column(primaryKey = true, autoIncrement = true, realName = ContactsContract.Contacts._ID)
    public long id;

    /**
     * The display name for the contact.
     * <P>Type: TEXT</P>
     */
    @Column(realName = ContactsContract.Contacts.DISPLAY_NAME)
    public String displayName;

    /**
     * A URI that can be used to retrieve the contact's full-size photo.
     * If PHOTO_FILE_ID is not null, this will be populated with a URI based off
     * {@link ContactsContract.DisplayPhoto#CONTENT_URI}.  Otherwise, this will
     * be populated with the same value as {@link #photoThumbnailUri}.
     * <p>
     * <P>Type: TEXT</P>
     */
    @Column(realName = ContactsContract.Contacts.PHOTO_URI)
    public Uri photoUri;

    /**
     * A URI that can be used to retrieve a thumbnail of the contact's photo.
     * If the content provider does not differentiate between full-size photos
     * and thumbnail photos, {@link #photoThumbnailUri} and {@link #photoUri} can contain
     * the same value, but either both shall be null or both not null.
     *
     * <P>Type: TEXT</P>
     */
    @Column(realName = ContactsContract.Contacts.PHOTO_THUMBNAIL_URI)
    public Uri photoThumbnailUri;

    /**
     * The number of times a contact has been contacted
     */
    @Column(realName = ContactsContract.Contacts.TIMES_CONTACTED)
    public int timesContacted;

    /**
     * The last time a contact was contacted.
     */
    @Column(realName = ContactsContract.Contacts.LAST_TIME_CONTACTED)
    public int lastTimeContacted;

    /**
     * Is the contact starred?
     */
    @Column(realName = ContactsContract.Contacts.STARRED)
    public boolean starred;

    /**
     * URI for a custom ringtone associated with the contact. If null or missing,
     * the default ringtone is used.
     */
    @Column(realName = ContactsContract.Contacts.CUSTOM_RINGTONE)
    public Uri customRingtone;

    /**
     * An opaque value that contains hints on how to find the contact if
     * its row id changed as a result of a sync or aggregation.
     */
    @Column(realName = ContactsContract.Contacts.LOOKUP_KEY)
    public Uri lookupKey;

    /**
     * Flag that reflects the {@link ContactsContract.Groups#GROUP_VISIBLE} state of any
     * {@link ContactsContract.CommonDataKinds.GroupMembership} for this contact.
     */
    @Column(realName = ContactsContract.Contacts.IN_VISIBLE_GROUP)
    public String inVisibleGroup;

    /**
     * An indicator of whether this contact has at least one phone number.
     */
    @Column(realName = ContactsContract.Contacts.HAS_PHONE_NUMBER)
    public boolean hasPhoneNumber;
}
