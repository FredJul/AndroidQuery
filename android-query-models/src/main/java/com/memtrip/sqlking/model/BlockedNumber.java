package com.memtrip.sqlking.model;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.BlockedNumberContract;

import com.memtrip.sqlking.common.Column;
import com.memtrip.sqlking.common.Table;
import com.memtrip.sqlking.database.ContentDatabaseProvider;
import com.memtrip.sqlking.model.gen.Q;
import com.memtrip.sqlking.operation.function.Result;

@SuppressWarnings("unused")
@TargetApi(24)
@Table(realName = "blocked")
public class BlockedNumber {

    @Column(realName = BlockedNumberContract.BlockedNumbers.COLUMN_ID)
    public int id;

    @Column(realName = BlockedNumberContract.BlockedNumbers.COLUMN_E164_NUMBER)
    public String e164Number;

    @Column(realName = BlockedNumberContract.BlockedNumbers.COLUMN_ORIGINAL_NUMBER)
    public String originalNumber;

    public static ContentDatabaseProvider getContentDatabaseProvider(ContentResolver contentResolver) {
        return new ContentDatabaseProvider(contentResolver, BlockedNumberContract.AUTHORITY, new Q.DefaultResolver());
    }

    public static Result<Contact> fromCursor(Cursor cursor) {
        return new Result<>(Contact.class, new Q.DefaultResolver(), cursor);
    }
}
