package com.taccardi.zak.library.dagger;

import com.taccardi.zak.library.model.Dealer;
import com.taccardi.zak.library.model.ForceError;
import com.taccardi.zak.library.model.InMemoryDealer;
import com.taccardi.zak.library.pojo.Schedulers;

import dagger.Module;
import dagger.Provides;

/**
 * Handles dependencies for [Dealer]
 */
@Module
public class DealerModule {
    @Provides
    @DataScope
    public Dealer dealer(Schedulers schedulers) {
        return new InMemoryDealer(schedulers.getComp(), ForceError.SOMETIMES, 500);
    }
}
