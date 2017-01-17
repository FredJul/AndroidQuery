package net.frju.androidquery.sample.loader;

import android.content.Context;

import net.frju.androidquery.database.BaseSelectLoader;
import net.frju.androidquery.models.Contact;
import net.frju.androidquery.models.gen.Q;
import net.frju.androidquery.operation.condition.Where;
import net.frju.androidquery.operation.function.CursorResult;
import net.frju.androidquery.operation.keyword.OrderBy;

public class ContactsLoader extends BaseSelectLoader<Contact> {

    public ContactsLoader(Context context) {
        super(context);
    }

    @Override
    public CursorResult<Contact> doSelect() {
        return Q.CONTACT.select()
                .where(Where.field(Q.CONTACT.IN_VISIBLE_GROUP).isEqualTo(true))
                .orderBy(new OrderBy(Q.CONTACT.DISPLAY_NAME, OrderBy.Order.ASC, OrderBy.Collate.LOCALIZED))
                .query();
    }
}