package com.taccardi.zak.card_deck.presentation.base;

import android.os.Bundle;

import com.evernote.android.state.StateSaver;

/**
 * Provides instance saving state.
 */
public class StateSaverActivityDelegate {
    private final BaseActivity activity;

    public StateSaverActivityDelegate(BaseActivity activity) {
        this.activity = activity;
    }

    public void onCreate(Bundle savedInstanceState) {
        StateSaver.restoreInstanceState(activity, savedInstanceState);
    }

    public void onSaveInstanceState(Bundle outState) {
        StateSaver.saveInstanceState(activity, outState);
    }
}
