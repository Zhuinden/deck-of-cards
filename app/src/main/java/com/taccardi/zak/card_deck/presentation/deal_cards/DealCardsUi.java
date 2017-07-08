package com.taccardi.zak.card_deck.presentation.deal_cards;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.VisibleForTesting;
import android.support.v7.util.DiffUtil;

import com.taccardi.zak.card_deck.presentation.base.StateRenderer;
import com.taccardi.zak.card_deck.presentation.deal_cards.CardsRecycler.Item;
import com.taccardi.zak.library.pojo.Card;
import com.taccardi.zak.library.pojo.Deck;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import paperparcel.PaperParcel;

/**
 * The user interface for dealing cards.
 *
 * @see DealCardsActivity
 */
public interface DealCardsUi
        extends StateRenderer<DealCardsUi.State> {
    public static class ObservableUtils {
        private ObservableUtils() {
        }

        public static <T, R> Observable<R> scanMap(Observable<T> _this, BiFunction<T, T, R> func2) {
            return _this.startWith((T) null) //emit a null value first, otherwise the .buffer() below won't emit at first (needs 2 emissions to emit)
                    .buffer(2, 1) //buffer the previous and current emission
                    .filter(ts -> ts.size() >= 2) //when the buffer terminates (onCompleted/onError), the remaining buffer is emitted. When don't want those!
                    .map(ts -> func2.apply(ts.get(0), ts.get(1)));
        }

        public static <T, R> Observable<R> scanMap(Observable<T> _this, T initialValue, BiFunction<T, T, R> func2) {
            return _this.startWith(initialValue) //emit a null value first, otherwise the .buffer() below won't emit at first (needs 2 emissions to emit)
                    .buffer(2, 1) //buffer the previous and current emission
                    .filter(ts -> ts.size() >= 2) //when the buffer terminates (onCompleted/onError), the remaining buffer is emitted. When don't want those!
                    .map(ts -> func2.apply(ts.get(0), ts.get(1)));
        }
    }

    public static class Nullable<T> {
        private final T value;

        public Nullable() {
            this(null);
        }

        public Nullable(T value) {
            this.value = value;
        }

        public boolean isNull() {
            return value == null;
        }

        public boolean isNonNull() {
            return !isNull();
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        public static <T> Nullable<T> toNullable(T _this) {
            return new Nullable<T>(_this);
        }

        public static <T, R> Observable<Nullable<R>> mapNullable(Observable<T> _this, Function<T, R> func) {
            return _this.map(t -> new Nullable<>(func.apply(t)));
        }

        public T getValue() {
            return value;
        }

        @Override
        public boolean equals(Object o) {
            if(this == o) {
                return true;
            }
            if(o == null || getClass() != o.getClass()) {
                return false;
            }

            Nullable<?> nullable = (Nullable<?>) o;

            return value != null ? value.equals(nullable.value) : nullable.value == null;
        }

        @Override
        public int hashCode() {
            return value != null ? value.hashCode() : 0;
        }
    }

    public static class RecyclerViewBinding<T> {
        private final List<T> newList;
        private final DiffUtil.DiffResult diff;

        public RecyclerViewBinding(List<T> newList, DiffUtil.DiffResult diff) {
            this.newList = newList;
            this.diff = diff;
        }

        public List<T> getNew() {
            return newList;
        }

        public DiffUtil.DiffResult getDiff() {
            return diff;
        }
    }

    State getState();

    void render(State state);

    public interface Intentions {
        /**
         * When the user requests to deal the top card from the deck
         */
        Observable<Object> dealCardRequests();

        /**
         * When the user requests to shuffle the remaining cards in the deck
         */
        Observable<Object> shuffleDeckRequests();

        /**
         * When the user requests a new deck
         */
        Observable<Object> newDeckRequests();
    }

    public interface Actions {
        /**
         * The the number of remaining cards in the deck
         */
        void showRemainingCards(int remainingCards);

        /**
         *
         */
        void showDeck(RecyclerViewBinding<Item> diff);

        /**
         * Show or hide the loading UI
         *
         * @param isLoading true to show the loading UI, false to hide it
         */
        void showLoading(boolean isLoading);

        /**
         * Disable or enable the buttons that allow user input
         *
         * @param disable true if buttons should be disabled, false if they should be enabled
         */
        void disableButtons(boolean disable);

        /**
         * Hide the error text
         */
        void hideError();

        /**
         * @param error text to display to user
         */
        void showError(String error);
    }

    @PaperParcel // (1)
    public static class State // TODO hashCode equals
            implements Parcelable { // (2)
        public static final Creator<State> CREATOR = PaperParcelDealCardsUi_State.CREATOR; // (3)

        private final Deck deck;
        private final boolean isShuffling;
        private final boolean isDealing;
        private final boolean isBuildingNewDeck;
        private final String error;

        private final transient boolean isLoading;

        public boolean isLoading() {
            return isLoading;
        }

        public Deck getDeck() {
            return deck;
        }

        public boolean isShuffling() {
            return isShuffling;
        }

        public boolean isDealing() {
            return isDealing;
        }

        public boolean isBuildingNewDeck() {
            return isBuildingNewDeck;
        }

        public String getError() {
            return error;
        }


        public int getRemaining() {
            return deck.getRemaining().size();
        }

        public List<Card> getDealt() {
            return deck.getDealt();
        }

        public State(Deck deck, boolean isShuffling, boolean isDealing, boolean isBuildingNewDeck, String error) {
            this.deck = deck;
            this.isShuffling = isShuffling;
            this.isDealing = isDealing;
            this.isBuildingNewDeck = isBuildingNewDeck;
            this.error = error;

            isLoading = isShuffling || isDealing || isBuildingNewDeck;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            PaperParcelDealCardsUi_State.writeToParcel(this, dest, flags); // (4)
        }

        public StateBuilder toBuilder() {
            return new StateBuilder().setDeck(deck)
                    .setIsShuffling(isShuffling)
                    .setIsDealing(isDealing)
                    .setIsBuildingNewDeck(isBuildingNewDeck)
                    .setError(error);
        }

        @Override
        public String toString() {
            return "State{isShuffling=" + isShuffling + ", isDealing=" + isDealing + ", isBuildingNewDeck=" + isBuildingNewDeck + ", error='" + error + '\'' + '}';
        }

        public static class StateBuilder {
            private Deck deck;
            private boolean isShuffling;
            private boolean isDealing;
            private boolean isBuildingNewDeck;
            private String error;

            public StateBuilder setDeck(Deck deck) {
                this.deck = deck;
                return this;
            }

            public StateBuilder setIsShuffling(boolean isShuffling) {
                this.isShuffling = isShuffling;
                return this;
            }

            public StateBuilder setIsDealing(boolean isDealing) {
                this.isDealing = isDealing;
                return this;
            }

            public StateBuilder setIsBuildingNewDeck(boolean isBuildingNewDeck) {
                this.isBuildingNewDeck = isBuildingNewDeck;
                return this;
            }

            public StateBuilder setError(String error) {
                this.error = error;
                return this;
            }

            public State build() {
                return new State(deck, isShuffling, isDealing, isBuildingNewDeck, error);
            }
        }

        /**
         * @return a new instance of [State], with a [Change] applied to it
         */
        public State reduce(Change change) {
            if(change instanceof Change.NoOp) {
                return this;
            }
            if(change instanceof Change.RequestShuffle) {
                return this.toBuilder().setIsShuffling(true).setError(null).build();
            }
            if(change instanceof Change.RequestTopCard) {
                return this.toBuilder().setIsDealing(true).setError(null).build();
            }
            if(change instanceof Change.RequestNewDeck) {
                return this.toBuilder().setIsBuildingNewDeck(true).setError(null).build();
            }
            if(change instanceof Change.DeckModified) {
                return this.toBuilder().setDeck(((Change.DeckModified) change).getDeck()).build();
            }
            if(change instanceof Change.Error) {
                Change.Error error = (Change.Error) change;
                if(error.source == ErrorSource.SHUFFLING) {
                    return this.toBuilder().setError(error.getDescription()).setIsShuffling(false).build();
                }
                if(error.source == ErrorSource.DEALING) {
                    return this.toBuilder().setError(error.getDescription()).setIsDealing(false).build();
                }
                if(error.source == ErrorSource.BUILDING_NEW_DECK) {
                    return this.toBuilder().setError(error.getDescription()).setIsBuildingNewDeck(false).build();
                }
                return this.toBuilder().setError(error.getDescription()).build();
            }
            if(change instanceof Change.IsDealing) {
                return this.toBuilder().setIsDealing(true).build();
            }
            if(change instanceof Change.IsShuffling) {
                return this.toBuilder().setIsShuffling(true).build();
            }
            if(change instanceof Change.IsBuildingDeck) {
                return this.toBuilder().setIsBuildingNewDeck(true).build();
            }
            if(change instanceof Change.DealingComplete) {
                return this.toBuilder().setIsDealing(false).build();
            }
            if(change instanceof Change.ShuffleComplete) {
                return this.toBuilder().setIsShuffling(false).build();
            }
            if(change instanceof Change.BuildingDeckComplete) {
                return this.toBuilder().setIsBuildingNewDeck(false).build();
            }
            if(change instanceof Change.DismissedError) {
                return this.toBuilder().setError(null).build();
            }
            return this;
        }

        public static abstract class Change { // TODO hashCode equals toString()
            private final String logText;

            public Change(String logText) {
                this.logText = logText;
            }

            public String getLogText() {
                return logText;
            }

            @Override
            public String toString() {
                return getClass().getSimpleName() + "[" + logText + "]";
            }

            public static final class RequestShuffle
                    extends Change {
                public RequestShuffle() {
                    super("user -> requested shuffle");
                }
            }

            public static final class RequestTopCard
                    extends Change {
                public RequestTopCard() {
                    super("user -> request top card of deck to be dealt");
                }
            }

            public static final class RequestNewDeck
                    extends Change {
                public RequestNewDeck() {
                    super("user -> requesting a new deck");
                }
            }

            public static final class Error
                    extends Change {
                private final ErrorSource source;
                private final String description;

                public Error(ErrorSource source, String description) {
                    super("error -> [" + description + "])");
                    this.source = source;
                    this.description = description;
                }

                public String getDescription() {
                    return description;
                }
            }

            public static final class NoOp
                    extends Change {
                public NoOp() {
                    super("");
                }
            }

            public static final class DeckModified
                    extends Change {
                private final Deck deck;

                public Deck getDeck() {
                    return deck;
                }

                public DeckModified(Deck deck) {
                    super(("disk -> deck changed. [" + deck.getRemaining()
                            .size() + "] cards remaining. [" + deck.getDealt().size() + "] cards dealt."));
                    this.deck = deck;
                }
            }

            public static final class IsDealing
                    extends Change {
                public IsDealing() {
                    super("network -> card is being dealt");
                }
            }


            public static final class IsShuffling
                    extends Change {
                public IsShuffling() {
                    super("network -> deck is being shuffled");
                }
            }

            public static final class IsBuildingDeck
                    extends Change {
                public IsBuildingDeck() {
                    super("network -> deck is being build");
                }
            }

            public static final class DealingComplete
                    extends Change {
                public DealingComplete() {
                    super("network -> card was successfully dealt");
                }
            }

            public static final class ShuffleComplete
                    extends Change {
                public ShuffleComplete() {
                    super("network -> deck was successfully shuffled");
                }
            }

            public static final class BuildingDeckComplete
                    extends Change {
                public BuildingDeckComplete() {
                    super("network -> deck was successfully built");
                }
            }

            public static final class DismissedError
                    extends Change {
                public DismissedError() {
                    super("user -> dismissedError");
                }
            }
        }

        enum ErrorSource {
            SHUFFLING,
            DEALING,
            BUILDING_NEW_DECK
        }

        public static final State NO_CARDS_DEALT = new StateBuilder() //
                .setDeck(Deck.FRESH_DECK) //
                .setIsShuffling(false) //
                .setIsDealing(false) //
                .setIsBuildingNewDeck(false) //
                .setError(null) //
                .build();

        public static final State DEFAULT = NO_CARDS_DEALT;

        @VisibleForTesting
        public static final State EVERY_CARD_DEALT = NO_CARDS_DEALT.toBuilder().setDeck(Deck.EVERY_CARD_DEALT).build();
    }
}