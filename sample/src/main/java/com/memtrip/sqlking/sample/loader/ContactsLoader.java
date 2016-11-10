package com.memtrip.sqlking.sample.loader;

import android.content.Context;

import com.memtrip.sqlking.database.BaseSelectLoader;
import com.memtrip.sqlking.database.Result;
import com.memtrip.sqlking.gen.Q;
import com.memtrip.sqlking.operation.function.Select;
import com.memtrip.sqlking.sample.model.Contacts;

public class ContactsLoader extends BaseSelectLoader<Contacts> {

    public ContactsLoader(Context context) {
        super(context);
    }

    @Override
    public Result<Contacts> doSelect() {
        return Select.getBuilder().execute(
                Contacts.class,
                Contacts.getContentDatabaseProvider(getContext().getContentResolver(), new Q.DefaultResolver()));
    }
}