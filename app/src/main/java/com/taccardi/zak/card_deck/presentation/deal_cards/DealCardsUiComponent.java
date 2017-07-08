package com.taccardi.zak.card_deck.presentation.deal_cards;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jakewharton.rxrelay2.Relay;
import com.taccardi.zak.card_deck.app.ActivityScope;

import javax.inject.Named;

import dagger.Subcomponent;

@Subcomponent(modules = {DealCardsUiModule.class})
@ActivityScope
public interface DealCardsUiComponent {
    public static final String NEW_DECK = "deal_card_new_deck";
    public static final String DEAL_CARD = "deal_card_first_item_in_recycler";
    public static final String SHUFFLE_DECK = "deal_card_shuffle";
    public static final String CARDS_LEFT = "deal_cards_remaining";
    public static final String PROGRESS_BAR = "deal_cards_progress_bar";
    public static final String ERROR = "deal_cards_error";
    public static final String DEAL_CARDS_LAYOUT = "deal_cards_layout";

    void injectMembers(DealCardsActivity dealCardsActivity);

    DealCardsUiRenderer renderer();

    CardsRecycler cards();

    DealCardsPresenter presenter();

    @Named(DealCardsUiComponent.DEAL_CARD)
    Relay<Object> dealCardClicks();

    @Named(DealCardsUiComponent.SHUFFLE_DECK)
    Relay<Object> shuffleDeckClicks();

    @Named(DealCardsUiComponent.NEW_DECK)
    Relay<Object> newDeckRequests();

    @Named(DealCardsUiComponent.NEW_DECK)
    View newDeckButton();

    @Named(DealCardsUiComponent.SHUFFLE_DECK)
    View shuffleButton();

    @Named(DealCardsUiComponent.CARDS_LEFT)
    TextView cardsLeftHint();

    @Named(DealCardsUiComponent.PROGRESS_BAR)
    ProgressBar progressBar();

    @Named(DealCardsUiComponent.DEAL_CARDS_LAYOUT)
    ViewGroup dealCardsUi();

    @Named(DealCardsUiComponent.ERROR)
    TextView error();
}