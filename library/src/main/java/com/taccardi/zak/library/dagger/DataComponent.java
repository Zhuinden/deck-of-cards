package com.taccardi.zak.library.dagger;

import com.taccardi.zak.library.model.Dealer;
import com.taccardi.zak.library.pojo.Schedulers;

import dagger.Component;

/**
 * Created by Zhuinden on 2017.07.08..
 */
@DataScope
@Component(modules = {DealerModule.class, ThreadingModule.class})
public interface DataComponent {
    Dealer dealer();

    Schedulers schedulers();
}
