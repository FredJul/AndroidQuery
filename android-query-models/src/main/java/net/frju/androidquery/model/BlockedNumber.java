package net.frju.androidquery.model;

import android.annotation.TargetApi;
import android.provider.BlockedNumberContract;

import net.frju.androidquery.annotation.Column;
import net.frju.androidquery.annotation.Table;

@SuppressWarnings("unused")
@TargetApi(24)
@Table(realName = "blocked", contentDatabaseProvider = BlockedNumberContentDatabaseProvider.class)
public class BlockedNumber {

    /**
     * Auto-generated ID field which monotonically increases.
     */
    @Column(primaryKey = true, autoIncrement = true, realName = BlockedNumberContract.BlockedNumbers.COLUMN_ID)
    public long id;

    /**
     * Phone number to block.  The system generates it from {@link #originalNumber}
     * by removing all formatting characters.
     * <p>Optional in {@code insert}.  When not specified, the system tries to generate it
     * assuming the current country. (Which will still be null if the number is not valid.)
     */
    @Column(realName = BlockedNumberContract.BlockedNumbers.COLUMN_E164_NUMBER)
    public String e164Number;

    /**
     * Phone number to block.
     * <p>Must be specified in {@code insert}.
     */
    @Column(realName = BlockedNumberContract.BlockedNumbers.COLUMN_ORIGINAL_NUMBER)
    public String originalNumber;
}
