package com.memtrip.sqlking.model;

import android.annotation.TargetApi;
import android.provider.BlockedNumberContract;

import com.memtrip.sqlking.common.Column;
import com.memtrip.sqlking.common.Table;

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
}
