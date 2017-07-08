package com.taccardi.zak.card_deck.presentation.deal_cards;

import android.support.v7.util.DiffUtil;

import com.jakewharton.rxrelay2.PublishRelay;
import com.jakewharton.rxrelay2.Relay;
import com.taccardi.zak.card_deck.presentation.base.StateRenderer;
import com.taccardi.zak.library.pojo.Card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.disposables.CompositeDisposable;

public class DealCardsUiRenderer
        implements StateRenderer<DealCardsUi.State> {
    private final DealCardsUi.Actions uiActions;
    private final Scheduler main;
    private final Scheduler comp;

    public DealCardsUiRenderer(DealCardsUi.Actions uiActions, Scheduler main, Scheduler comp) {
        this.uiActions = uiActions;
        this.main = main;
        this.comp = comp;
        start();
    }

    private final CompositeDisposable disposables = new CompositeDisposable();

    private final Relay<DealCardsUi.State> state = PublishRelay.<DealCardsUi.State>create().toSerialized();

    public void start() {
        disposables.add(state //
                .map(DealCardsUi.State::getRemaining) //
                .distinctUntilChanged() //
                .subscribeOn(comp)//
                .observeOn(main) //
                .subscribe(uiActions::showRemainingCards));

        Observable<List<CardsRecycler.Item>> itemObservable = state //
                .map(DealCardsUi.State::getDealt) //
                .distinctUntilChanged() //
                .map((cards) -> { //
                    List<CardsRecycler.Item.UiCard> uiCards = new LinkedList<>();
                    for(Card card : cards) {
                        uiCards.add(new CardsRecycler.Item.UiCard(card));
                    }
                    return new ArrayList<>(uiCards);
                }) //
                .map(cards -> {
                    List<CardsRecycler.Item> list = new ArrayList<>(cards.size() + 1);
                    list.add(new CardsRecycler.Item.UiDeck());
                    list.addAll(cards);
                    return list;
                });
        disposables.add(DealCardsUi.ObservableUtils.scanMap(itemObservable, //
                Collections.emptyList(), DealCardsUiRenderer::calculateDiff) //
                .subscribeOn(comp) //
                .observeOn(main) //
                .subscribe(uiActions::showDeck)); //

        disposables.add(state.map(DealCardsUi.State::isLoading) //
                .distinctUntilChanged() //
                .subscribeOn(comp) //
                .observeOn(main) //
                .subscribe(isLoading -> {
                    uiActions.showLoading(isLoading);
                    uiActions.disableButtons(isLoading);
                }));

        disposables.add(DealCardsUi.Nullable.mapNullable(state, DealCardsUi.State::getError) //
                .distinctUntilChanged() //
                .subscribeOn(comp) //
                .observeOn(main) //
                .subscribe(stringNullable -> {
                    String error = stringNullable.getValue();
                    if(error == null) {
                        uiActions.hideError();
                    } else {
                        uiActions.showError(error);
                    }
                }));
    }

    @Override
    public void render(DealCardsUi.State state) {
        this.state.accept(state);
    }

    public void stop() {
        disposables.clear();
    }

    public static DealCardsUi.RecyclerViewBinding<CardsRecycler.Item> calculateDiff(List<CardsRecycler.Item> oldList, List<CardsRecycler.Item> newList) {
        return new DealCardsUi.RecyclerViewBinding<>(newList, DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return oldList.size();
            }

            @Override
            public int getNewListSize() {
                return newList.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                CardsRecycler.Item oldItem = oldList.get(oldItemPosition);
                CardsRecycler.Item newItem = newList.get(newItemPosition);
                return oldItem.isItemSame(newItem);
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                CardsRecycler.Item oldItem = oldList.get(oldItemPosition);
                CardsRecycler.Item newItem = newList.get(newItemPosition);
                return oldItem.isContentSame(newItem);
            }
        }));
    }
}