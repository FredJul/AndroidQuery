package net.frju.androidquery.model;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import net.frju.androidquery.annotation.Column;
import net.frju.androidquery.annotation.Table;
import net.frju.androidquery.models.gen.Q;
import net.frju.androidquery.operation.function.Count;
import net.frju.androidquery.operation.function.CursorResult;
import net.frju.androidquery.operation.function.Delete;
import net.frju.androidquery.operation.function.Insert;
import net.frju.androidquery.operation.function.Save;
import net.frju.androidquery.operation.function.Select;
import net.frju.androidquery.operation.function.Update;

import java.util.List;

@SuppressWarnings("unused")
@Table(realName = "raw_contacts", contentDatabaseProvider = ContactContentDatabaseProvider.class)
public class RawContact {

    /**
     * The unique ID for a row.
     */
    @Column(realName = ContactsContract.RawContacts._ID)
    public int id;

    /**
     * An optional name that can be used in the UI to represent this directory,
     * e.g. "Acme Corp"
     * <p>TYPE: text</p>
     */
    @Column(realName = ContactsContract.RawContacts.CONTACT_ID)
    public int contactId;

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
     * <p>
     *
     * @see ContactsContract.ContactNameColumns#DISPLAY_NAME_ALTERNATIVE
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

    public static void init(Context context) {
        Q.init(context);
    }

    public static Count.Builder<RawContact> count() {
        return Q.RawContact.count();
    }

    public static Select.Builder<RawContact> select() {
        return Q.RawContact.selectViaContentProvider();
    }

    public static Delete.Builder<RawContact> delete() {
        return Q.RawContact.deleteViaContentProvider();
    }

    public static Insert.Builder<RawContact> insert(RawContact... models) {
        return Q.RawContact.insertViaContentProvider(models);
    }

    public static Insert.Builder<RawContact> insert(List<RawContact> models) {
        return Q.RawContact.insertViaContentProvider(models);
    }

    public static Update.Builder<RawContact> update() {
        return Q.RawContact.updateViaContentProvider();
    }

    public static Save.Builder<RawContact> save(RawContact... models) {
        return Q.RawContact.saveViaContentProvider(models);
    }

    public static Save.Builder<RawContact> save(List<RawContact> models) {
        return Q.RawContact.saveViaContentProvider(models);
    }

    public static CursorResult<RawContact> fromCursor(Cursor cursor) {
        return Q.RawContact.fromCursor(cursor);
    }
}
