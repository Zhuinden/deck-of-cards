package com.taccardi.zak.library.model;

import android.support.annotation.VisibleForTesting;

import com.jakewharton.rxrelay2.BehaviorRelay;
import com.taccardi.zak.library.pojo.Deck;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.functions.Action;

/**
 * Created by Zhuinden on 2017.07.08..
 */


public class InMemoryDealer
        implements Dealer {
    private static final Object UNIT = new Object();

    private final Scheduler comp;
    private final ForceError forceError;
    private final long delayMs;

    public InMemoryDealer(Scheduler scheduler) {
        this(scheduler, ForceError.NEVER, 0);
    }

    public InMemoryDealer(Scheduler scheduler, ForceError forceError, long delayMs) {
        this.comp = scheduler;
        this.forceError = forceError;
        this.delayMs = delayMs;
    }

    private Deck deck = Deck.FRESH_DECK;

    public void setDeck(Deck deck) {
        this.deck = deck;
        decks.accept(deck);
    }

    @VisibleForTesting
    BehaviorRelay<Deck> decks = BehaviorRelay.createDefault(deck);
    @VisibleForTesting
    BehaviorRelay<DealOperation> dealOperations = BehaviorRelay.create();
    @VisibleForTesting
    BehaviorRelay<ShuffleOperation> shuffleOperations = BehaviorRelay.create();
    @VisibleForTesting
    BehaviorRelay<BuildingDeckOperation> buildingDeckOperations = BehaviorRelay.create();

    /**
     * Randomly call one of two methods based oin probability.
     *
     * @param failRate between 0-100. This is the % [onFail] will be called.
     */
    private void randomCall(int failRate, Action success, Action failure) {
        if(failRate < 0 || failRate > 100) {
            throw new IllegalStateException("Fail rate should be between 0 and 100. Was [" + failRate + "]");
        }

        int result = new Random().nextInt(100);

        if(failRate > result) {
            try {
                failure.run();
            } catch(Exception e) {
                Exceptions.throwIfFatal(e);
            }
        } else {
            try {
                success.run();
            } catch(Exception e) {
                Exceptions.throwIfFatal(e);
            }
        }
    }

    private Action topCardSuccess = () -> {
        Deck newDeck = deck.withDealtCard();
        dealOperations.accept(new DealOperation.TopCard(newDeck.getLastCardDealt()));
        setDeck(newDeck);
    };

    private Action topCardFailure = () -> {
        dealOperations.accept(new DealOperation.Error("Failed to deal top card. Try again"));
    };

    @Override
    public void dealTopCard() {
        Observable.just(UNIT)
                .doOnNext(o -> dealOperations.accept(new DealOperation.Dealing()))
                .delay(delayMs, TimeUnit.MILLISECONDS, comp)
                .doOnNext(o -> {
                    if(forceError == ForceError.NEVER) {
                        topCardSuccess.run();
                        return;
                    } else if(forceError == ForceError.SOMETIMES) {
                        randomCall(10, topCardSuccess, topCardFailure);
                        return;
                    } else if(forceError == ForceError.ALWAYS) {
                        topCardFailure.run();
                        return;
                    }
                    throw new IllegalStateException("Illegal force error [" + forceError + "]");
                })
                .subscribe();
    }

    private Action shuffleSuccess = () -> {
        Deck shuffled = deck.toShuffled();
        shuffleOperations.accept(new ShuffleOperation.Shuffled(shuffled));
        setDeck(shuffled);
    };

    private Action shuffleFailure = () -> {
        shuffleOperations.accept(new ShuffleOperation.Error("Shuffle failed. Try again"));
    };

    @Override
    public void shuffleDeck() {
        Observable.just(UNIT) //
                .doOnNext(o -> shuffleOperations.accept(new ShuffleOperation.Shuffling()))
                .delay(delayMs, TimeUnit.MILLISECONDS, comp)
                .doOnNext(o -> {
                    if(forceError == ForceError.NEVER) {
                        shuffleSuccess.run();
                        return;
                    } else if(forceError == ForceError.SOMETIMES) {
                        randomCall(10, shuffleSuccess, shuffleFailure);
                        return;
                    } else if(forceError == ForceError.ALWAYS) {
                        shuffleFailure.run();
                        return;
                    }
                    throw new IllegalStateException("Invalid force error [" + forceError + "]");
                })
                .subscribe();
    }

    private Action requestNewDeckSuccess = () -> {
        Deck fresh = Deck.FRESH_DECK;
        buildingDeckOperations.accept(new BuildingDeckOperation.Built(fresh));
        setDeck(fresh);
    };

    private Action requestNewDeckFailure = () -> {
        buildingDeckOperations.accept(new BuildingDeckOperation.Error("Building new deck failed. Try again"));
    };

    @Override
    public void requestNewDeck() {
        Observable.just(UNIT) //
                .doOnNext(o -> buildingDeckOperations.accept(new BuildingDeckOperation.Building()))
                .delay(delayMs, TimeUnit.MILLISECONDS, comp)
                .doOnNext(o -> {
                    if(forceError == ForceError.NEVER) {
                        requestNewDeckSuccess.run();
                        return;
                    } else if(forceError == ForceError.SOMETIMES) {
                        randomCall(10, requestNewDeckSuccess, requestNewDeckFailure);
                        return;
                    } else if(forceError == ForceError.ALWAYS) {
                        requestNewDeckFailure.run();
                        return;
                    }
                    throw new IllegalStateException("Invalid force error [" + forceError + "]");
                })
                .subscribe();
    }

    @Override
    public Observable<Deck> getDecks() {
        return decks;
    }

    @Override
    public Observable<DealOperation> getDealOperations() {
        return dealOperations;
    }

    @Override
    public Observable<BuildingDeckOperation> getBuildingDeckOperations() {
        return buildingDeckOperations;
    }

    @Override
    public Observable<ShuffleOperation> getShuffleOperations() {
        return shuffleOperations;
    }
}