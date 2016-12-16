package net.frju.androidquery.models;

import android.content.Context;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;

import net.frju.androidquery.database.BaseContentDatabaseProvider;
import net.frju.androidquery.database.Resolver;
import net.frju.androidquery.models.gen.Q;


public class ContactContentDatabaseProvider extends BaseContentDatabaseProvider {

    public ContactContentDatabaseProvider(Context context) {
        super(context);
    }

    @Override
    protected
    @NonNull
    String getAuthority() {
        return ContactsContract.AUTHORITY;
    }

    @Override
    protected
    @NonNull
    Resolver getResolver() {
        return Q.getResolver();
    }
}
