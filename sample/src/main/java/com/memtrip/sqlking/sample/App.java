package com.memtrip.sqlking.sample;

import android.app.Application;

import net.frju.androidquery.gen.Q;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //TODO
        net.frju.androidquery.models.gen.Q.init(this);
        Q.init(this);
    }
}