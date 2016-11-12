package com.memtrip.sqlking.sample;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.memtrip.sqlking.database.ContentDatabaseProvider;
import com.memtrip.sqlking.database.LocalDatabaseProvider;
import com.memtrip.sqlking.sample.model.Comment;
import com.memtrip.sqlking.sample.model.User;
import com.memtrip.sqlking.sample.model.gen.Q;

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

        mContentDatabaseProvider = new Q.ContentDatabaseProvider(getContentResolver(),
                "com.memtrip.sqlking.sample.provider.ModelContentProvider"
        );

        sApp = this;
    }

    public static LocalDatabaseProvider getLocalDatabaseProvider(Context context) {
        if (sApp != null && sApp.mLocalDatabaseProvider != null) {
            return sApp.mLocalDatabaseProvider;
        } else {
            return new Q.LocalDatabaseProvider(context,
                    DATABASE_NAME,
                    VERSION,
                    Comment.class,
                    User.class) {

                @Override
                protected void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                    super.onUpgrade(db, oldVersion, newVersion);

                    // Put here your migration code
                }
            };
        }
    }
}