package net.frju.androidquery.sample;

import android.app.Application;

import net.frju.androidquery.gen.Q;
import net.frju.androidquery.model.Contact;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Contact.init(this);
        Q.init(this);
    }
}