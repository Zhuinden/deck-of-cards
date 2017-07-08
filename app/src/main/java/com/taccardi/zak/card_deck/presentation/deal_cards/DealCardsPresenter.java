package com.taccardi.zak.card_deck.presentation.deal_cards;

import com.taccardi.zak.card_deck.app.MyApplication;
import com.taccardi.zak.card_deck.presentation.deal_cards.DealCardsUi.State;
import com.taccardi.zak.card_deck.presentation.deal_cards.DealCardsUi.State.Change;
import com.taccardi.zak.card_deck.presentation.deal_cards.DealCardsUi.State.Change.Error;
import com.taccardi.zak.card_deck.presentation.deal_cards.DealCardsUi.State.ErrorSource;
import com.taccardi.zak.library.model.BuildingDeckOperation;
import com.taccardi.zak.library.model.DealOperation;
import com.taccardi.zak.library.model.Dealer;
import com.taccardi.zak.library.model.ShuffleOperation;
import com.taccardi.zak.library.pojo.Deck;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import timber.log.Timber;

/**
 * Presenter for [DealCardsUi]
 *
 * @property intentions user input events
 * @property renderer how we output state to the user
 * @property dealer our "data" layer.
 */
public class DealCardsPresenter {
    public static final String TAG = DealCardsUi.class.getSimpleName();

    private final DealCardsUi ui;
    private final DealCardsUi.Intentions intentions;
    private final Dealer dealer;

    public DealCardsPresenter(DealCardsUi ui, DealCardsUi.Intentions intentions, Dealer dealer) {
        this.ui = ui;
        this.intentions = intentions;
        this.dealer = dealer;
    }

    private final CompositeDisposable disposables = new CompositeDisposable();

    public void start() {
        if(!MyApplication.ACTIVATE_PRESENTERS) {
            return;
        }

        Observable<Change> shuffles = intentions.shuffleDeckRequests() //
                .map((Function<Object, Change>) o -> new Change.RequestShuffle()) //
                .doOnNext(o -> dealer.shuffleDeck()) // 
                .share() //
                .onErrorReturn(DealCardsPresenter::handleUnknownError);

        Observable<Change> newDeckRequests = intentions.newDeckRequests() //
                .map((Function<Object, Change>) o -> new Change.RequestNewDeck()) //
                .doOnNext(change -> dealer.requestNewDeck()) //
                .onErrorReturn(DealCardsPresenter::handleUnknownError);

        Observable<Change> dealCardRequests = intentions.dealCardRequests() //
                .map((Function<Object, Change>) o -> new Change.RequestTopCard()) //
                .doOnNext(change -> dealer.dealTopCard()) //
                .onErrorReturn(DealCardsPresenter::handleUnknownError);

        @SuppressWarnings("Convert2MethodRef") //
                Observable<Change> decks = dealer.getDecks() //
                .map((Function<Deck, Change>) deck -> new Change.DeckModified(deck)) //
                .onErrorReturn(DealCardsPresenter::handleUnknownError);

        Observable<Change> dealOperations = dealer.getDealOperations() //
                .map(DealCardsPresenter::handle) //
                .onErrorReturn(DealCardsPresenter::handleUnknownError);

        Observable<Change> shuffleOperations = dealer.getShuffleOperations() //
                .map(DealCardsPresenter::handle) //
                .onErrorReturn(DealCardsPresenter::handleUnknownError);

        Observable<Change> deckBuildingOperations = dealer.getBuildingDeckOperations() //
                .map(DealCardsPresenter::handle).onErrorReturn(DealCardsPresenter::handleUnknownError);

        Observable<Change> merged = shuffles.mergeWith(newDeckRequests)
                .mergeWith(dealCardRequests)
                .mergeWith(decks)
                .mergeWith(dealOperations)
                .mergeWith(shuffleOperations)
                .mergeWith(deckBuildingOperations).doOnNext(change -> {
                    Timber.tag(TAG);
                    Timber.d(change.getLogText());
                });


        disposables.add(merged.scan(ui.getState(), State::reduce).doOnNext(state -> {
            Timber.tag(TAG);
            Timber.v("    --- [" + state + "]");
        }).subscribe(ui::render));
    }

    public void stop() {
        disposables.clear();
    }

    private static Change handleUnknownError(Throwable throwable) {
        return new Change.Error(null, throwable.getLocalizedMessage());
    }

    private static Change handle(DealOperation operation) {
        if(operation instanceof DealOperation.Dealing) {
            return new Change.IsDealing();
        }
        if(operation instanceof DealOperation.Error) {
            return new Error(ErrorSource.DEALING, ((DealOperation.Error) operation).getDescription());
        }
        if(operation instanceof DealOperation.TopCard) {
            return new Change.DealingComplete();
        }
        throw new IllegalStateException("Unknown operation [" + operation + "]");
    }

    private static Change handle(ShuffleOperation operation) {
        if(operation instanceof ShuffleOperation.Shuffling) {
            return new Change.IsShuffling();
        }
        if(operation instanceof ShuffleOperation.Error) {
            return new Error(ErrorSource.SHUFFLING, ((ShuffleOperation.Error) operation).getDescription());
        }
        if(operation instanceof ShuffleOperation.Shuffled) {
            return new Change.ShuffleComplete();
        }
        throw new IllegalStateException("Unknown operation [" + operation + "]");
    }

    private static Change handle(BuildingDeckOperation operation) {
        if(operation instanceof BuildingDeckOperation.Building) {
            return new Change.IsBuildingDeck();
        }
        if(operation instanceof BuildingDeckOperation.Error) {
            return new Error(ErrorSource.BUILDING_NEW_DECK, ((BuildingDeckOperation.Error) operation).getDescription());
        }
        if(operation instanceof BuildingDeckOperation.Built) {
            return new Change.BuildingDeckComplete();
        }
        throw new IllegalStateException("Unknown operation [" + operation + "]");
    }
}
