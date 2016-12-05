package net.frju.androidquery.sample;

import android.app.Application;

import net.frju.androidquery.gen.Q;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        net.frju.androidquery.models.gen.Q.init(this);
        Q.init(this);
    }
}