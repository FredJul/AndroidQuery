package com.memtrip.sqlking.model;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.provider.BlockedNumberContract;

import com.memtrip.sqlking.common.Column;
import com.memtrip.sqlking.common.Table;
import com.memtrip.sqlking.operation.function.Count;
import com.memtrip.sqlking.operation.function.Delete;
import com.memtrip.sqlking.operation.function.Insert;
import com.memtrip.sqlking.operation.function.Result;
import com.memtrip.sqlking.operation.function.Select;

import net.frju.androidquery.models.gen.Q;

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
        return Q.BlockedNumber.deleteWithContentProvider();
    }

    public static Insert.Builder<BlockedNumber> insert(BlockedNumber... models) {
        return Q.BlockedNumber.insertWithContentProvider(models);
    }

    public static Result<BlockedNumber> fromCursor(Cursor cursor) {
        return Q.BlockedNumber.fromCursor(cursor);
    }
}
