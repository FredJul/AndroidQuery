package net.frju.androidquery.models;

import android.content.Context;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

import net.frju.androidquery.database.BaseContentDatabaseProvider;
import net.frju.androidquery.database.Resolver;
import net.frju.androidquery.models.gen.Q;


public class MediaStoreContentDatabaseProvider extends BaseContentDatabaseProvider {

    public MediaStoreContentDatabaseProvider(Context context) {
        super(context);
    }

    @Override
    protected
    @NonNull
    String getAuthority() {
        return MediaStore.AUTHORITY;
    }

    @Override
    protected
    @NonNull
    Resolver getResolver() {
        return Q.getResolver();
    }
}
