package com.memtrip.sqlking.sample.loader;

import android.content.Context;

import com.memtrip.sqlking.database.BaseSelectLoader;
import com.memtrip.sqlking.model.Contact;
import com.memtrip.sqlking.operation.function.Result;
import com.memtrip.sqlking.operation.function.Select;

public class ContactsLoader extends BaseSelectLoader<Contact> {

    public ContactsLoader(Context context) {
        super(context);
    }

    @Override
    public Result<Contact> doSelect() {
        return Select.getBuilder().execute(
                Contact.class,
                Contact.getContentDatabaseProvider(getContext().getContentResolver()));
    }
}