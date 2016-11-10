package com.memtrip.sqlking.sample;

import android.app.Application;
import android.content.Context;

import com.memtrip.sqlking.database.ContentDatabaseProvider;
import com.memtrip.sqlking.database.LocalDatabaseProvider;
import com.memtrip.sqlking.gen.Q;
import com.memtrip.sqlking.sample.model.Comment;
import com.memtrip.sqlking.sample.model.User;

public class App extends Application {
    private static App sApp;

    private LocalDatabaseProvider mLocalDatabaseProvider;
    private ContentDatabaseProvider mContentDatabaseProvider;

    private static final String DATABASE_NAME = "SQLKing";
    private static final int VERSION = 2;

    public LocalDatabaseProvider getLocalDatabaseProvider() {
        return mLocalDatabaseProvider;
    }

    public ContentDatabaseProvider getContentDatabaseProvider() {
        return mContentDatabaseProvider;
    }

    public static App getInstance() {
        return sApp;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mLocalDatabaseProvider = getLocalDatabaseProvider(this);

        mContentDatabaseProvider = new ContentDatabaseProvider(getContentResolver(),
                "com.memtrip.sqlking.sample.provider.ModelContentProvider",
                new Q.DefaultResolver()
        );

        sApp = this;
    }

    public static LocalDatabaseProvider getLocalDatabaseProvider(Context context) {
        if (sApp != null && sApp.mLocalDatabaseProvider != null) {
            return sApp.mLocalDatabaseProvider;
        } else {
            return new LocalDatabaseProvider(context,
                    DATABASE_NAME,
                    VERSION,
                    new Q.DefaultResolver(),
                    Comment.class,
                    User.class);
        }
    }
}