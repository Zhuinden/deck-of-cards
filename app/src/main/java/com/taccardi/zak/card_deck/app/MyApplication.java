package com.taccardi.zak.card_deck.app;

import android.app.Application;
import android.content.Context;

import com.taccardi.zak.card_deck.BuildConfig;
import com.taccardi.zak.library.dagger.DaggerDataComponent;
import com.taccardi.zak.library.dagger.DataComponent;

import timber.log.Timber;


/**
 * Overridden application.
 */
public class MyApplication
        extends Application {
    AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        buildComponent();
    }

    public static MyApplication get(Context context) {
        return (MyApplication) context.getApplicationContext();
    }

    public AppComponent buildComponent() {
        appComponent = DaggerAppComponent.builder().dataComponent(buildDataComponent()).build();
        return appComponent;
    }

    protected void setInTesting(boolean isInTestMode) {
        if (isInTestMode) {
            ACTIVATE_PRESENTERS = false;
        }
    }

    protected DataComponent buildDataComponent() {
        return DaggerDataComponent.builder().build();
    }

    public static boolean ACTIVATE_PRESENTERS = true;

    public AppComponent getComponent() {
        return appComponent;
    }
}
