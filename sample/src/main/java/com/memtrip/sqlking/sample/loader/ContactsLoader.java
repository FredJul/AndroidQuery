package com.memtrip.sqlking.sample.loader;

import android.content.Context;

import com.memtrip.sqlking.database.BaseSelectLoader;
import com.memtrip.sqlking.gen.Q;
import com.memtrip.sqlking.operation.function.Result;
import com.memtrip.sqlking.operation.function.Select;
import com.memtrip.sqlking.sample.model.Contact;

public class ContactsLoader extends BaseSelectLoader<Contact> {

    public ContactsLoader(Context context) {
        super(context);
    }

    @Override
    public Result<Contact> doSelect() {
        return Select.getBuilder().execute(
                Contact.class,
                Contact.getContentDatabaseProvider(getContext().getContentResolver(), new Q.DefaultResolver()));
    }
}