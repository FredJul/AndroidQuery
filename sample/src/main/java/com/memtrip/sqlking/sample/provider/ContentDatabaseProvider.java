package com.memtrip.sqlking.sample.provider;

import android.content.ContentResolver;

import com.memtrip.sqlking.database.BaseContentDatabaseProvider;
import com.memtrip.sqlking.database.Resolver;

import net.frju.androidquery.gen.Q;


public class ContentDatabaseProvider extends BaseContentDatabaseProvider {

    public ContentDatabaseProvider(ContentResolver contentResolver) {
        super(contentResolver);
    }

    @Override
    protected String getAuthority() {
        return "com.memtrip.sqlking.sample.provider.ModelContentProvider";
    }

    @Override
    protected Resolver getResolver() {
        return Q.getResolver();
    }
}
