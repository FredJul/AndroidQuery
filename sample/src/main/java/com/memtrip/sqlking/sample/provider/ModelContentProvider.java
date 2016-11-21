package com.memtrip.sqlking.sample.provider;

import com.memtrip.sqlking.database.BaseContentProvider;
import com.memtrip.sqlking.database.BaseLocalDatabaseProvider;
import com.memtrip.sqlking.sample.model.Comment;

import net.frju.androidquery.gen.Q;

public class ModelContentProvider extends BaseContentProvider {

    @Override
    protected BaseLocalDatabaseProvider getLocalSQLProvider() {
        Q.init(getContext());
        return Q.getResolver().getLocalDatabaseProviderForModel(Comment.class);
    }
}
