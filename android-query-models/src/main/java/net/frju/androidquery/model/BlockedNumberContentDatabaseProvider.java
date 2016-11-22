package net.frju.androidquery.model;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.provider.BlockedNumberContract;

import net.frju.androidquery.database.BaseContentDatabaseProvider;
import net.frju.androidquery.database.Resolver;
import net.frju.androidquery.models.gen.Q;

@TargetApi(24)
public class BlockedNumberContentDatabaseProvider extends BaseContentDatabaseProvider {

    public BlockedNumberContentDatabaseProvider(ContentResolver contentResolver) {
        super(contentResolver);
    }

    @Override
    protected String getAuthority() {
        return BlockedNumberContract.AUTHORITY;
    }

    @Override
    protected Resolver getResolver() {
        return Q.getResolver();
    }
}
