package net.frju.androidquery.model;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.provider.BlockedNumberContract;

import net.frju.androidquery.annotation.Column;
import net.frju.androidquery.annotation.Table;
import net.frju.androidquery.models.gen.Q;
import net.frju.androidquery.operation.function.Count;
import net.frju.androidquery.operation.function.Delete;
import net.frju.androidquery.operation.function.Insert;
import net.frju.androidquery.operation.function.Result;
import net.frju.androidquery.operation.function.Select;

@SuppressWarnings("unused")
@TargetApi(24)
@Table(realName = "blocked", contentDatabaseProvider = BlockedNumberContentDatabaseProvider.class)
public class BlockedNumber {

    @Column(realName = BlockedNumberContract.BlockedNumbers.COLUMN_ID)
    public int id;

    @Column(realName = BlockedNumberContract.BlockedNumbers.COLUMN_E164_NUMBER)
    public String e164Number;

    @Column(realName = BlockedNumberContract.BlockedNumbers.COLUMN_ORIGINAL_NUMBER)
    public String originalNumber;

    public static void init(Context context) {
        Q.init(context);
    }

    public static Count.Builder<BlockedNumber> count() {
        return Q.BlockedNumber.count();
    }

    public static Select.Builder<BlockedNumber> select() {
        return Q.BlockedNumber.select();
    }

    public static Delete.Builder<BlockedNumber> delete() {
        return Q.BlockedNumber.deleteViaContentProvider();
    }

    public static Insert.Builder<BlockedNumber> insert(BlockedNumber... models) {
        return Q.BlockedNumber.insertViaContentProvider(models);
    }

    public static Result<BlockedNumber> fromCursor(Cursor cursor) {
        return Q.BlockedNumber.fromCursor(cursor);
    }
}
