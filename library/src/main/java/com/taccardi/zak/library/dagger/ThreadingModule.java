package com.taccardi.zak.library.dagger;

import dagger.Module;
import dagger.Provides;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * A module for threading
 */
@Module
public class ThreadingModule {
    private final com.taccardi.zak.library.pojo.Schedulers schedulers;

    public ThreadingModule() {
        this(null);
    }

    public ThreadingModule(com.taccardi.zak.library.pojo.Schedulers schedulers) {
        this.schedulers = schedulers;
    }


    @Provides
    @DataScope
    public com.taccardi.zak.library.pojo.Schedulers schedulers() {
        return schedulers == null ? new com.taccardi.zak.library.pojo.Schedulers(main(),
                disk(),
                comp(),
                network()) : schedulers;
    }

    //    @Provides @Named(DataLayer.MAIN) @DataScope
    Scheduler main() {
        return AndroidSchedulers.mainThread();
    }

    //    @Provides @DataScope @Named(DataLayer.DISK)
    Scheduler disk() {
        return Schedulers.io();
    }

    //    @Provides @DataScope @Named(DataLayer.NETWORK)
    Scheduler network() {
        return disk();
    }

    //    @Provides @DataScope @Named(DataLayer.COMP)
    Scheduler comp() {
        return Schedulers.computation();
    }

    //    @Provides @DataScope @Named(DataLayer.TRAMPOLINE)
    Scheduler trampoline() {
        return Schedulers.trampoline();
    }
}





