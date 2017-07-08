package com.taccardi.zak.card_deck.presentation.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.taccardi.zak.card_deck.app.MyApplication;

/**
 * Base activity for all activities to inherit from.
 */
public abstract class BaseActivity
        extends AppCompatActivity {
    @SuppressWarnings("LeakingThis")
    private final StateSaverActivityDelegate state = new StateSaverActivityDelegate(this);

    public MyApplication getMyApplication() {
        return MyApplication.get(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        state.onCreate(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        state.onSaveInstanceState(outState);
    }
}
