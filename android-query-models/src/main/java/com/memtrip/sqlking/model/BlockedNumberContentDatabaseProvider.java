package com.memtrip.sqlking.model;

import android.content.ContentResolver;
import android.provider.BlockedNumberContract;

import com.memtrip.sqlking.database.BaseContentDatabaseProvider;
import com.memtrip.sqlking.database.Resolver;

import net.frju.androidquery.models.gen.Q;


public class BlockedNumberContentDatabaseProvider extends BaseContentDatabaseProvider {

    public BlockedNumberContentDatabaseProvider(ContentResolver contentResolver) {
        super(contentResolver);
    }

    @Override
    protected String getAuthority() {
        return BlockedNumberContract.AUTHORITY;
    }

    @Override
    protected Resolver getResolver() {
        return Q.getResolver();
    }
}
