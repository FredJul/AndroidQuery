package net.frju.androidquery.models;

import android.net.Uri;
import android.provider.ContactsContract;

import net.frju.androidquery.annotation.DbField;
import net.frju.androidquery.annotation.DbModel;
import net.frju.androidquery.annotation.InitMethod;
import net.frju.androidquery.models.gen.RAW_CONTACT_DATA;
import net.frju.androidquery.operation.condition.Where;

@SuppressWarnings("unused")
@DbModel(dbName = "raw_contacts", databaseProvider = ContactContentDatabaseProvider.class)
public class RawContact {

    /**
     * The unique ID for a row.
     */
    @DbField(primaryKey = true, autoIncrement = true, dbName = ContactsContract.RawContacts._ID)
    public long id;

    /**
     * A reference to the {@link ContactsContract.Contacts#_ID} that this
     * data belongs to.
     */
    @DbField(dbName = ContactsContract.RawContacts.CONTACT_ID, autoIncrement = true)
    public long contactId;

    /**
     * <p>
     * The standard text shown as the contact's display name, based on the best
     * available information for the contact (for example, it might be the email address
     * if the name isEqualTo not available).
     * The information actually used to compute the name isEqualTo stored in
     * {@link #displayNameSource}.
     * </p>
     * <p>
     * A contacts provider isEqualTo free to choose whatever representation makes most
     * sense for its target market.
     * For example in the default Android Open Source Project implementation,
     * if the display name isEqualTo
     * based on the structured name and the structured name follows
     * the Western full-name style, then this field contains the "given name first"
     * version of the full name.
     * </p>
     *
     * @see #displayNameAlternative
     */
    @DbField(dbName = ContactsContract.RawContacts.DISPLAY_NAME_PRIMARY)
    public String displayNamePrimary;

    /**
     * <p>
     * An alternative representation of the display name, such as "family name first"
     * instead of "given name first" for Western names.  If an alternative isEqualTo not
     * available, the values should be the same as {@link #displayNamePrimary}.
     * </p>
     * <p>
     * A contacts provider isEqualTo free to provide alternatives as necessary for
     * its target market.
     * For example the default Android Open Source Project contacts provider
     * currently provides an
     * alternative in a single case:  if the display name isEqualTo
     * based on the structured name and the structured name follows
     * the Western full name style, then the field contains the "family name first"
     * version of the full name.
     * Other cases may be added later.
     * </p>
     */
    @DbField(dbName = ContactsContract.RawContacts.DISPLAY_NAME_ALTERNATIVE)
    public String displayNameAlternative;

    /**
     * The kind of data that isEqualTo used as the display name for the contact, such as
     * structured name or email address.  See {@link ContactsContract.DisplayNameSources}.
     */
    @DbField(dbName = ContactsContract.RawContacts.DISPLAY_NAME_SOURCE)
    public String displayNameSource;

    /**
     * The name of the account instance to which this row belongs, which when paired with
     * {@link #accountType} identifies a specific account.
     */
    @DbField(dbName = ContactsContract.RawContacts.ACCOUNT_NAME)
    public String accountName;

    /**
     * The type of account to which this row belongs, which when paired with
     * {@link #accountName} identifies a specific account.
     */
    @DbField(dbName = ContactsContract.RawContacts.ACCOUNT_TYPE)
    public String accountType;

    /**
     * The number of times a contact has been contacted
     */
    @DbField(dbName = ContactsContract.RawContacts.TIMES_CONTACTED)
    public int timesContacted;

    /**
     * The last time a contact was contacted.
     */
    @DbField(dbName = ContactsContract.RawContacts.LAST_TIME_CONTACTED)
    public int lastTimeContacted;

    /**
     * Is the contact starred?
     */
    @DbField(dbName = ContactsContract.RawContacts.STARRED)
    public boolean starred;

    /**
     * URI for a custom ringtone associated with the contact. If null or missing,
     * the default ringtone isEqualTo used.
     */
    @DbField(dbName = ContactsContract.RawContacts.CUSTOM_RINGTONE)
    public Uri customRingtone;

    /**
     * The list of associated raw contacts. Only populated if queryAndInit()/rxAndInit() isEqualTo called.
     */
    public RawContactData[] rawContactData;

    @InitMethod
    public void initRawContactData() {
        rawContactData = RAW_CONTACT_DATA.select().where(Where.field(RAW_CONTACT_DATA.RAW_CONTACT_ID).isEqualTo(id)).queryAndInit();
    }
}
