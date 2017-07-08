package com.taccardi.zak.card_deck.app;

import com.taccardi.zak.card_deck.presentation.deal_cards.DealCardsUiComponent;
import com.taccardi.zak.card_deck.presentation.deal_cards.DealCardsUiModule;
import com.taccardi.zak.library.dagger.DataComponent;

import dagger.Component;

/**
 * Dagger component to provide application wide dependencies
 */
@AppScope
@Component(modules = {AppModule.class}, dependencies = {DataComponent.class})
public interface AppComponent
        extends DataComponent {
    DealCardsUiComponent plus(DealCardsUiModule module);
}

