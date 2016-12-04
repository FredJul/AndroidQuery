package net.frju.androidquery.model;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.provider.BlockedNumberContract;

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
@TargetApi(24)
@Table(realName = "blocked", contentDatabaseProvider = BlockedNumberContentDatabaseProvider.class)
public class BlockedNumber {

    /**
     * Auto-generated ID field which monotonically increases.
     */
    @Column(realName = BlockedNumberContract.BlockedNumbers.COLUMN_ID)
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

    public static void init(Context context) {
        Q.init(context);
    }

    public static Count.Builder<BlockedNumber> count() {
        return Q.BlockedNumber.count();
    }

    public static Select.Builder<BlockedNumber> select() {
        return Q.BlockedNumber.selectViaContentProvider();
    }

    public static Delete.Builder<BlockedNumber> delete() {
        return Q.BlockedNumber.deleteViaContentProvider();
    }

    public static Insert.Builder<BlockedNumber> insert(BlockedNumber... models) {
        return Q.BlockedNumber.insertViaContentProvider(models);
    }

    public static Insert.Builder<BlockedNumber> insert(List<BlockedNumber> models) {
        return Q.BlockedNumber.insertViaContentProvider(models);
    }

    public static Update.Builder<BlockedNumber> update() {
        return Q.BlockedNumber.updateViaContentProvider();
    }

    public static Save.Builder<BlockedNumber> save(BlockedNumber... models) {
        return Q.BlockedNumber.saveViaContentProvider(models);
    }

    public static Save.Builder<BlockedNumber> save(List<BlockedNumber> models) {
        return Q.BlockedNumber.saveViaContentProvider(models);
    }

    public static CursorResult<BlockedNumber> fromCursor(Cursor cursor) {
        return Q.BlockedNumber.fromCursor(cursor);
    }
}
