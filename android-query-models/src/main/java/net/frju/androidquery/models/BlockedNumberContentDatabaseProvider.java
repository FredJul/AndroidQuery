package net.frju.androidquery.models;

import android.annotation.TargetApi;
import android.content.Context;
import android.provider.BlockedNumberContract;
import android.support.annotation.NonNull;

import net.frju.androidquery.database.BaseContentDatabaseProvider;
import net.frju.androidquery.database.Resolver;
import net.frju.androidquery.models.gen.Q;

@TargetApi(24)
public class BlockedNumberContentDatabaseProvider extends BaseContentDatabaseProvider {

    public BlockedNumberContentDatabaseProvider(Context context) {
        super(context);
    }

    @Override
    protected
    @NonNull
    String getAuthority() {
        return BlockedNumberContract.AUTHORITY;
    }

    @Override
    protected
    @NonNull
    Resolver getResolver() {
        return Q.getResolver();
    }
}
