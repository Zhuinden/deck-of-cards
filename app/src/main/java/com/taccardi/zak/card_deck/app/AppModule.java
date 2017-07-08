package com.taccardi.zak.card_deck.app;

import android.app.Application;

import dagger.Module;
import dagger.Provides;

/**
 * Provides application level dependencies.
 *
 * @see AppComponent
 */
@Module
public class AppModule {
    private final MyApplication application;

    public AppModule(MyApplication application) {
        this.application = application;
    }

    @Provides @AppScope
    public Application application() {
        return application;
    }

    @Provides @AppScope
    public MyApplication myApplication() {
        return application;
    }
}
