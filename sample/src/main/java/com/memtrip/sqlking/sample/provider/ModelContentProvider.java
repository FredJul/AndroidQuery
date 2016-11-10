package com.memtrip.sqlking.sample.provider;

import com.memtrip.sqlking.database.BaseContentProvider;
import com.memtrip.sqlking.database.LocalDatabaseProvider;
import com.memtrip.sqlking.sample.App;

public class ModelContentProvider extends BaseContentProvider {

    @Override
    protected LocalDatabaseProvider getLocalSQLProvider() {
        return App.getLocalDatabaseProvider(getContext());
    }
}
