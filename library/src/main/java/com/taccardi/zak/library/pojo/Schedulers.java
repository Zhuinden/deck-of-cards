package com.taccardi.zak.library.pojo;

import com.taccardi.zak.library.dagger.DataLayer;
import com.taccardi.zak.library.dagger.DataScope;

import javax.inject.Named;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * A single point of entry for injecting all our schedulers.
 */
@DataScope
public class Schedulers {
    private final Scheduler main;
    private final Scheduler disk;
    private final Scheduler comp;
    private final Scheduler network;

    public Schedulers(@Named(DataLayer.MAIN) Scheduler main, @Named(DataLayer.DISK) Scheduler disk, @Named(DataLayer.COMP) Scheduler comp, @Named(DataLayer.NETWORK) Scheduler network) {
        this.main = main;
        this.disk = disk;
        this.comp = comp;
        this.network = network;
    }

    public static Schedulers createTrampoline() {
        return createTrampoline(false);
    }

    public static Schedulers createTrampoline(boolean useMain) {
        return new Schedulers(useMain ? AndroidSchedulers.mainThread() : io.reactivex.schedulers.Schedulers.trampoline(),
                io.reactivex.schedulers.Schedulers.trampoline(),
                io.reactivex.schedulers.Schedulers.trampoline(),
                io.reactivex.schedulers.Schedulers.trampoline());
    }

    public Scheduler getMain() {
        return main;
    }

    public Scheduler getDisk() {
        return disk;
    }

    public Scheduler getComp() {
        return comp;
    }

    public Scheduler getNetwork() {
        return network;
    }
}
