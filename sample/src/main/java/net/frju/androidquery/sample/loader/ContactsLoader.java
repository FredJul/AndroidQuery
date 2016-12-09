package net.frju.androidquery.sample.loader;

import android.content.Context;

import net.frju.androidquery.database.BaseSelectLoader;
import net.frju.androidquery.models.Contact;
import net.frju.androidquery.models.gen.Q;
import net.frju.androidquery.operation.function.CursorResult;

public class ContactsLoader extends BaseSelectLoader<Contact> {

    public ContactsLoader(Context context) {
        super(context);
    }

    @Override
    public CursorResult<Contact> doSelect() {
        return Q.Contact.selectViaContentProvider().query();
    }
}