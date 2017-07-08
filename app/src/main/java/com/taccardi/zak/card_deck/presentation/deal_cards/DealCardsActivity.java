package com.taccardi.zak.card_deck.presentation.deal_cards;

import android.content.Context;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jakewharton.rxrelay2.Relay;
import com.taccardi.zak.card_deck.R;
import com.taccardi.zak.card_deck.app.MyApplication;
import com.taccardi.zak.card_deck.presentation.base.BaseActivity;
import com.taccardi.zak.card_deck.presentation.base.StateRenderer;

import javax.inject.Inject;
import javax.inject.Named;

import io.reactivex.Observable;

public class DealCardsActivity
        extends BaseActivity
        implements DealCardsUi, DealCardsUi.Actions, DealCardsUi.Intentions, StateRenderer<DealCardsUi.State> {
    private final Object UNIT = new Object();

    @com.evernote.android.state.State
    private DealCardsUi.State state = DealCardsUi.State.NO_CARDS_DEALT;

    DealCardsUiComponent component;
    @Inject
    CardsRecycler cards;
    @Inject
    DealCardsUiRenderer renderer;
    @Inject
    DealCardsPresenter presenter;
    @Inject
    @Named(DealCardsUiComponent.DEAL_CARD)
    Relay<Object> dealCardClicks;
    @Inject
    @Named(DealCardsUiComponent.SHUFFLE_DECK)
    Relay<Object> shuffleDeckClicks;
    @Inject
    @Named(DealCardsUiComponent.NEW_DECK)
    Relay<Object> newDeckRequests;
    @Inject
    @Named(DealCardsUiComponent.CARDS_LEFT)
    TextView cardsLeftHint;
    @Inject
    @Named(DealCardsUiComponent.PROGRESS_BAR)
    ProgressBar progressBar;
    @Inject
    @Named(DealCardsUiComponent.NEW_DECK)
    View newDeckButton;
    @Inject
    @Named(DealCardsUiComponent.ERROR)
    TextView error;
    @Inject
    @Named(DealCardsUiComponent.SHUFFLE_DECK)
    View shuffleButton;
    @Inject
    @Named(DealCardsUiComponent.DEAL_CARDS_LAYOUT)
    ViewGroup dealCardsUi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cards);

        component = MyApplication.get(this).getComponent().plus(new DealCardsUiModule(this));

        component.injectMembers(this);

        newDeckButton.setOnClickListener(v -> newDeckRequests.accept(UNIT));
        shuffleButton.setOnClickListener(v -> shuffleDeckClicks.accept(UNIT));
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.start();
    }

    @Override
    protected void onStop() {
        presenter.stop();
        super.onStop();
    }

    @Override
    public void showError(String error) {
        Transition transition = new AutoTransition();
        TransitionManager.beginDelayedTransition(dealCardsUi, transition);
        this.error.setText(error);
        this.error.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideError() {
        Transition transition = new AutoTransition();
        TransitionManager.beginDelayedTransition(dealCardsUi, transition);
        this.error.setVisibility(View.GONE);
    }

    @Override
    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    @Override
    public void render(DealCardsUi.State state) {
        this.state = state;
        renderer.render(state);
    }

    public static String remainingCardsHint(Context context, int count) {
        if(count == 0) {
            return context.getString(R.string.dealCardsUi_remainingCards_hint_zero);
        }

        return context.getResources().getQuantityString(R.plurals.number_of_cards_left, count, count);
    }

    @Override
    public void showRemainingCards(int remainingCards) {
        cardsLeftHint.setText(remainingCardsHint(this, remainingCards));
    }

    @Override
    public Observable<Object> dealCardRequests() {
        return dealCardClicks.filter(unit -> !state.isLoading());
    }

    @Override
    public Observable<Object> shuffleDeckRequests() {
        return shuffleDeckClicks.filter(o -> !state.isLoading());
    }

    @Override
    public Observable<Object> newDeckRequests() {
        return newDeckRequests.filter(o -> !state.isLoading());
    }

    @Override
    public void showLoading(boolean isLoading) {
        if(isLoading) {
            progressBar.setIndeterminate(true);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void showDeck(RecyclerViewBinding<CardsRecycler.Item> diff) {
        cards.showDeck(diff);
    }

    @Override
    public void disableButtons(boolean disable) {
        newDeckButton.setEnabled(!disable);
        shuffleButton.setEnabled(!disable);
    }
}