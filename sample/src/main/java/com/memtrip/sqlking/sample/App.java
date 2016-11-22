package com.memtrip.sqlking.sample;

import android.app.Application;

import com.memtrip.sqlking.model.Contact;

import net.frju.androidquery.gen.Q;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Contact.init(this);
        Q.init(this);
    }
}