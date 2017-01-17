package net.frju.androidquery.models;

import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.RequiresApi;

import net.frju.androidquery.annotation.DbField;
import net.frju.androidquery.annotation.DbModel;

@SuppressWarnings("unused")
@DbModel(dbName = "data", databaseProvider = ContactContentDatabaseProvider.class)
public class RawContactData {

    public enum MimeType {
        @RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH)
        IDENTITY(ContactsContract.CommonDataKinds.Identity.CONTENT_ITEM_TYPE),
        NAME(ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE),
        PHONE(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE),
        EMAIL(ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE),
        PHOTO(ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE),
        ORGANISATION(ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE),
        IM(ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE),
        NICKNAME(ContactsContract.CommonDataKinds.Nickname.CONTENT_ITEM_TYPE),
        NOTE(ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE),
        POSTAL(ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE),
        GROUP(ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE),
        WEBSITE(ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE),
        EVENT(ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE),
        RELATION(ContactsContract.CommonDataKinds.Relation.CONTENT_ITEM_TYPE),
        SIP(ContactsContract.CommonDataKinds.SipAddress.CONTENT_ITEM_TYPE);

        private final String type;

        MimeType(final String type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return type;
        }

        public static MimeType fromString(String type) {
            if (type != null) {
                for (MimeType item : MimeType.values()) {
                    if (type.equals(item.type)) {
                        return item;
                    }
                }
            }
            return null;
        }
    }

    public enum NicknameType {
        DEFAULT(ContactsContract.CommonDataKinds.Nickname.TYPE_DEFAULT),
        OTHER_NAME(ContactsContract.CommonDataKinds.Nickname.TYPE_OTHER_NAME),
        MAIDEN_NAME(ContactsContract.CommonDataKinds.Nickname.TYPE_MAIDEN_NAME),
        SHORT_NAME(ContactsContract.CommonDataKinds.Nickname.TYPE_SHORT_NAME),
        INITIALS(ContactsContract.CommonDataKinds.Nickname.TYPE_INITIALS);

        private final int type;

        NicknameType(final int text) {
            this.type = text;
        }

        public int toInt() {
            return type;
        }

        public static NicknameType fromInt(int type) {
            for (NicknameType item : NicknameType.values()) {
                if (type == item.type) {
                    return item;
                }
            }
            return null;
        }
    }

    public enum PhoneType {
        HOME(ContactsContract.CommonDataKinds.Phone.TYPE_HOME),
        MOBILE(ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE),
        WORK(ContactsContract.CommonDataKinds.Phone.TYPE_WORK),
        FAX_WORK(ContactsContract.CommonDataKinds.Phone.TYPE_FAX_WORK),
        FAX_HOME(ContactsContract.CommonDataKinds.Phone.TYPE_FAX_HOME),
        PAGER(ContactsContract.CommonDataKinds.Phone.TYPE_PAGER),
        OTHER(ContactsContract.CommonDataKinds.Phone.TYPE_OTHER),
        CALLBACK(ContactsContract.CommonDataKinds.Phone.TYPE_CALLBACK),
        CAR(ContactsContract.CommonDataKinds.Phone.TYPE_CAR),
        COMPANY_MAIN(ContactsContract.CommonDataKinds.Phone.TYPE_COMPANY_MAIN),
        ISDN(ContactsContract.CommonDataKinds.Phone.TYPE_ISDN),
        MAIN(ContactsContract.CommonDataKinds.Phone.TYPE_MAIN),
        OTHER_FAX(ContactsContract.CommonDataKinds.Phone.TYPE_OTHER_FAX),
        RADIO(ContactsContract.CommonDataKinds.Phone.TYPE_RADIO),
        TELEX(ContactsContract.CommonDataKinds.Phone.TYPE_TELEX),
        TTY_TDD(ContactsContract.CommonDataKinds.Phone.TYPE_TTY_TDD),
        WORK_MOBILE(ContactsContract.CommonDataKinds.Phone.TYPE_WORK_MOBILE),
        WORK_PAGER(ContactsContract.CommonDataKinds.Phone.TYPE_WORK_PAGER),
        ASSISTANT(ContactsContract.CommonDataKinds.Phone.TYPE_ASSISTANT),
        MMS(ContactsContract.CommonDataKinds.Phone.TYPE_MMS);

        private final int type;

        PhoneType(final int text) {
            this.type = text;
        }

        public int toInt() {
            return type;
        }

        public static PhoneType fromInt(int type) {
            for (PhoneType item : PhoneType.values()) {
                if (type == item.type) {
                    return item;
                }
            }
            return null;
        }
    }

    /**
     * The unique ID for a row.
     */
    @DbField(primaryKey = true, autoIncrement = true, dbName = ContactsContract.RawContacts._ID)
    public long id;

    /**
     * A reference to the {@link ContactsContract.RawContacts#_ID}
     * that this data belongs to.
     */
    @DbField(dbName = ContactsContract.Data.RAW_CONTACT_ID, autoIncrement = true)
    public long rawContactId;

    /**
     * The MIME type of the item represented by this row. One of the {@link MimeType}.toString() types.
     */
    @DbField(dbName = ContactsContract.Data.MIMETYPE)
    public MimeType mimeType;

    /**
     * Whether this isEqualTo the primary entry of its kind for the raw contact it belongs to.
     */
    @DbField(dbName = ContactsContract.Data.IS_PRIMARY)
    public boolean isPrimary;

    /**
     * Whether this isEqualTo the primary entry of its kind for the aggregate
     * contact it belongs to. Any data record that isEqualTo "super primary" must
     * also be "primary".
     */
    @DbField(dbName = ContactsContract.Data.IS_SUPER_PRIMARY)
    public boolean isSuperPrimary;

    /**
     * The version of this data record. This isEqualTo a read-only value. The data column isEqualTo
     * guaranteed to not change without the version going up. This value isEqualTo monotonically
     * increasing.
     */
    @DbField(dbName = ContactsContract.Data.DATA_VERSION)
    public int dataVersion;

    @DbField(dbName = ContactsContract.Data.DATA1)
    public String data1;
    @DbField(dbName = ContactsContract.Data.DATA2)
    public String data2;
    @DbField(dbName = ContactsContract.Data.DATA3)
    public String data3;
    @DbField(dbName = ContactsContract.Data.DATA4)
    public String data4;
    @DbField(dbName = ContactsContract.Data.DATA5)
    public String data5;
    @DbField(dbName = ContactsContract.Data.DATA6)
    public String data6;
    @DbField(dbName = ContactsContract.Data.DATA7)
    public String data7;
    @DbField(dbName = ContactsContract.Data.DATA8)
    public String data8;
    @DbField(dbName = ContactsContract.Data.DATA9)
    public String data9;
    @DbField(dbName = ContactsContract.Data.DATA10)
    public String data10;
    @DbField(dbName = ContactsContract.Data.DATA11)
    public String data11;
    @DbField(dbName = ContactsContract.Data.DATA12)
    public String data12;
    @DbField(dbName = ContactsContract.Data.DATA13)
    public String data13;
    @DbField(dbName = ContactsContract.Data.DATA14)
    public String data14;
    @DbField(dbName = ContactsContract.Data.DATA15)
    public byte[] data15;

    /**
     * The name that should be used to display the contact.
     * <i>Unstructured component of the name should be consistent with
     * its structured representation.</i>
     */
    public String getName_displayName() {
        return data1;
    }

    /**
     * The given name for the contact.
     */
    public String getName_givenName() {
        return data2;
    }

    /**
     * The family name for the contact.
     */
    public String getName_familyName() {
        return data3;
    }

    /**
     * The contact's honorific prefix, e.g. "Sir"
     */
    public String getName_prefix() {
        return data4;
    }

    /**
     * The contact's middle name
     */
    public String getName_middleName() {
        return data5;
    }

    /**
     * The contact's honorific suffix, e.g. "Jr"
     */
    public String getName_suffix() {
        return data6;
    }

    /**
     * The phonetic version of the given name for the contact.
     */
    public String getName_phoneticGivenName() {
        return data7;
    }

    /**
     * The phonetic version of the additional name for the contact.
     */
    public String getName_phoneticMiddleName() {
        return data8;
    }

    /**
     * The phonetic version of the family name for the contact.
     */
    public String getName_phoneticFamilyName() {
        return data9;
    }

    /**
     * The style used for combining given/middle/family name into a full name.
     * See {@link ContactsContract.FullNameStyle}.
     */
    public String getName_fullNameStyle() {
        return data10;
    }

    /**
     * The alphabet used for capturing the phonetic name.
     * See ContactsContract.PhoneticNameStyle.
     */
    public String getName_phoneticNameStyle() {
        return data11;
    }


    /**
     * The name itself
     */
    public String getNickname_name() {
        return data1;
    }

    /**
     * The nickname type. One of the {@link NicknameType} types.
     */
    public NicknameType getNickname_type() {
        return NicknameType.fromInt(Integer.valueOf(data2));
    }


    /**
     * The phone number as the user entered it.
     */
    public String getPhone_number() {
        return data1;
    }

    /**
     * The phone number's E164 representation. This value can be omitted in which
     * case the provider will try to automatically infer it.  (It'll be left null if the
     * provider fails to infer.)
     * If present, {@link #getPhone_number()} has to be set as well (it will be ignored otherwise).
     */
    public String getPhone_normalizedNumber() {
        return data4;
    }

    /**
     * The phone type. One of the {@link PhoneType} types.
     */
    public PhoneType getPhone_type() {
        return PhoneType.fromInt(Integer.valueOf(data2));
    }
}
