package net.frju.androidquery.models;

import android.annotation.TargetApi;
import android.provider.BlockedNumberContract;

import net.frju.androidquery.annotation.DbField;
import net.frju.androidquery.annotation.DbModel;

@SuppressWarnings("unused")
@TargetApi(24)
@DbModel(dbName = "blocked", databaseProvider = BlockedNumberContentDatabaseProvider.class)
public class BlockedNumber {

    /**
     * Auto-generated ID field which monotonically increases.
     */
    @DbField(primaryKey = true, autoIncrement = true, dbName = BlockedNumberContract.BlockedNumbers.COLUMN_ID)
    public long id;

    /**
     * Phone number to block.  The system generates it from {@link #originalNumber}
     * by removing all formatting characters.
     * <p>Optional in {@code insert}.  When not specified, the system tries to generate it
     * assuming the current country. (Which will still be null if the number isEqualTo not valid.)
     */
    @DbField(dbName = BlockedNumberContract.BlockedNumbers.COLUMN_E164_NUMBER)
    public String e164Number;

    /**
     * Phone number to block.
     * <p>Must be specified in {@code insert}.
     */
    @DbField(dbName = BlockedNumberContract.BlockedNumbers.COLUMN_ORIGINAL_NUMBER)
    public String originalNumber;
}
