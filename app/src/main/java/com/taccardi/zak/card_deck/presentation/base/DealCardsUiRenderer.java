package com.taccardi.zak.card_deck.presentation.base;

import com.jakewharton.rxrelay2.PublishRelay;
import com.jakewharton.rxrelay2.Relay;
import com.taccardi.zak.card_deck.presentation.deal_cards.DealCardsUi;

import io.reactivex.Scheduler;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;

/**
 * Renders a [DealCardsUi.State] to the [DealCardsUi]
 */
public class DealCardsUiRenderer
        implements StateRenderer<DealCardsUi.State> {

    private final DealCardsUi.Actions uiActions;
    private final Scheduler main;
    private final Scheduler comp;

    public DealCardsUiRenderer(DealCardsUi.Actions uiActions, Scheduler main, Scheduler comp) {
        this.uiActions = uiActions;
        this.main = main;
        this.comp = comp;
    }

    private final CompositeDisposable disposables = new CompositeDisposable();
    //observable relay that represents the UI's state over time. Each emission
    //represents the latest state.
    private final Relay<DealCardsUi.State> stateRelay = PublishRelay.<DealCardsUi.State>create().toSerialized();

    /**
     * Binds a state pojo to the UI,
     *
     * @param state a pojo representing the latest state of the UI to be rendered
     */
    @Override
    public void render(DealCardsUi.State state) {
        this.stateRelay.accept(state);
    }

    public void start() {
        //observe loading state
        disposables.add(stateRelay
                //reduce state to whether it's loading (true/false)
                .map(DealCardsUi.State::isLoading)
                //only render load to UI
                .distinctUntilChanged()
                //handle all calculations on a background thread pool
                .subscribeOn(comp)
                //hop on the UI thread to render to the view
                .observeOn(main).subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(@NonNull Boolean isLoading)
                            throws Exception {
                        uiActions.showLoading(isLoading);
                        uiActions.disableButtons(isLoading);
                    }
                }));
    }
}
