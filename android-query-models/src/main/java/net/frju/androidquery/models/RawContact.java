package net.frju.androidquery.models;

import android.net.Uri;
import android.provider.ContactsContract;

import net.frju.androidquery.annotation.Column;
import net.frju.androidquery.annotation.Table;

@SuppressWarnings("unused")
@Table(realName = "raw_contacts", contentDatabaseProvider = ContactContentDatabaseProvider.class)
public class RawContact {

    /**
     * The unique ID for a row.
     */
    @Column(primaryKey = true, autoIncrement = true, realName = ContactsContract.RawContacts._ID)
    public long id;

    /**
     * A reference to the {@link ContactsContract.Contacts#_ID} that this
     * data belongs to.
     */
    @Column(realName = ContactsContract.RawContacts.CONTACT_ID, autoIncrement = true)
    public long contactId;

    /**
     * <p>
     * The standard text shown as the contact's display name, based on the best
     * available information for the contact (for example, it might be the email address
     * if the name is not available).
     * The information actually used to compute the name is stored in
     * {@link #displayNameSource}.
     * </p>
     * <p>
     * A contacts provider is free to choose whatever representation makes most
     * sense for its target market.
     * For example in the default Android Open Source Project implementation,
     * if the display name is
     * based on the structured name and the structured name follows
     * the Western full-name style, then this field contains the "given name first"
     * version of the full name.
     * </p>
     *
     * @see #displayNameAlternative
     */
    @Column(realName = ContactsContract.RawContacts.DISPLAY_NAME_PRIMARY)
    public String displayNamePrimary;

    /**
     * <p>
     * An alternative representation of the display name, such as "family name first"
     * instead of "given name first" for Western names.  If an alternative is not
     * available, the values should be the same as {@link #displayNamePrimary}.
     * </p>
     * <p>
     * A contacts provider is free to provide alternatives as necessary for
     * its target market.
     * For example the default Android Open Source Project contacts provider
     * currently provides an
     * alternative in a single case:  if the display name is
     * based on the structured name and the structured name follows
     * the Western full name style, then the field contains the "family name first"
     * version of the full name.
     * Other cases may be added later.
     * </p>
     */
    @Column(realName = ContactsContract.RawContacts.DISPLAY_NAME_ALTERNATIVE)
    public String displayNameAlternative;

    /**
     * The kind of data that is used as the display name for the contact, such as
     * structured name or email address.  See {@link ContactsContract.DisplayNameSources}.
     */
    @Column(realName = ContactsContract.RawContacts.DISPLAY_NAME_SOURCE)
    public String displayNameSource;

    /**
     * The name of the account instance to which this row belongs, which when paired with
     * {@link #accountType} identifies a specific account.
     */
    @Column(realName = ContactsContract.RawContacts.ACCOUNT_NAME)
    public String accountName;

    /**
     * The type of account to which this row belongs, which when paired with
     * {@link #accountName} identifies a specific account.
     */
    @Column(realName = ContactsContract.RawContacts.ACCOUNT_TYPE)
    public String accountType;

    /**
     * The number of times a contact has been contacted
     */
    @Column(realName = ContactsContract.RawContacts.TIMES_CONTACTED)
    public int timesContacted;

    /**
     * The last time a contact was contacted.
     */
    @Column(realName = ContactsContract.RawContacts.LAST_TIME_CONTACTED)
    public int lastTimeContacted;

    /**
     * Is the contact starred?
     */
    @Column(realName = ContactsContract.RawContacts.STARRED)
    public boolean starred;

    /**
     * URI for a custom ringtone associated with the contact. If null or missing,
     * the default ringtone is used.
     */
    @Column(realName = ContactsContract.RawContacts.CUSTOM_RINGTONE)
    public Uri customRingtone;
}
