package net.frju.androidquery.sample.provider;

import net.frju.androidquery.database.BaseContentProvider;
import net.frju.androidquery.database.BaseLocalDatabaseProvider;
import net.frju.androidquery.gen.Q;
import net.frju.androidquery.sample.model.Comment;

public class ModelContentProvider extends BaseContentProvider {

    @Override
    protected BaseLocalDatabaseProvider getLocalSQLProvider() {
        Q.init(getContext());
        return Q.getResolver().getLocalDatabaseProviderForModel(Comment.class);
    }
}
