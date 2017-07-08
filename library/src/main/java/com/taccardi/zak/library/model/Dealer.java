package com.taccardi.zak.library.model;

import com.taccardi.zak.library.pojo.Deck;

import io.reactivex.Observable;

/**
 * Interacts with the deck of cards
 */
public interface Dealer {
    void dealTopCard();

    void shuffleDeck();

    void requestNewDeck();

    Observable<Deck> getDecks();

    Observable<DealOperation> getDealOperations();

    Observable<BuildingDeckOperation> getBuildingDeckOperations();

    Observable<ShuffleOperation> getShuffleOperations();
}