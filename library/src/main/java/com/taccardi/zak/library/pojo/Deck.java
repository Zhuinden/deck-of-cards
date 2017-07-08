package com.taccardi.zak.library.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import paperparcel.PaperParcel;

/**
 * A deck of [Card]s
 */
@PaperParcel
public class Deck
        implements Parcelable { // TODO hashCode equals
    private final List<Card> remaining;
    private final List<Card> dealt;

    public Builder toBuilder() {
        return new Builder(this);
    }

    public List<Card> getRemaining() {
        return remaining;
    }

    public List<Card> getDealt() {
        return dealt;
    }

    @Override
    public String toString() {
        return "Deck{" + "remaining=" + Arrays.toString(remaining.toArray()) + ", dealt=" + Arrays.toString(dealt.toArray()) + '}';
    }

    public class Builder {
        private List<Card> remaining;
        private List<Card> dealt;

        public Builder() {
        }

        public Builder(Deck deck) {
            this.remaining = deck.remaining;
            this.dealt = deck.dealt;
        }

        public Builder setRemaining(List<Card> remaining) {
            this.remaining = remaining;
            return this;
        }

        public Builder setDealt(List<Card> dealt) {
            this.dealt = dealt;
            return this;
        }

        public Deck build() {
            return new Deck(remaining, dealt);
        }
    }

    public Deck(List<Card> remaining, List<Card> dealt) {
        this.remaining = remaining;
        this.dealt = dealt;
    }

    public Card getLastCardDealt() {
        return dealt.isEmpty() ? null : dealt.get(0);
    }

    /**
     * @return the top card of the deck, or null if it does not exist. This does not actually "deal" the top card.
     */
    public Card getTopCard() {
        return remaining.isEmpty() ? null : remaining.get(0);
    }


    /**
     * @return a new instance of this deck with a card dealt, or the same instance if no cards remain
     */
    public Deck withDealtCard() {
        if(!remaining.isEmpty()) {
            Stack<Card> cards = toStack(remaining);
            Card topCard = cards.pop();
            return new Builder().setRemaining(cards).setDealt(append(topCard, dealt)).build();
        }
        return this;
    }

    /**
     * @return a new instance of this deck with the remaining cards shuffled
     */
    public Deck toShuffled() {
        List<Card> shuffled = new ArrayList<>(remaining);
        Collections.shuffle(shuffled);
        return this.toBuilder().setRemaining(shuffled).build();
    }

    public static final Creator<Deck> CREATOR = PaperParcelDeck.CREATOR;

    public static final Deck FRESH_DECK;

    static {
        Stack<Card> cards = new Stack<Card>();
        for(Suit suit : Suit.values()) {
            for(Rank rank : Rank.values()) {
                cards.add(new Card(rank, suit));
            }
        }
        FRESH_DECK = new Deck(cards, Collections.emptyList());
    }

    public static final Deck EVERY_CARD_DEALT;

    static {
        Stack<Card> cards = new Stack<Card>();
        for(Suit suit : Suit.values()) {
            for(Rank rank : Rank.values()) {
                cards.add(new Card(rank, suit));
            }
        }

        EVERY_CARD_DEALT = new Deck(Collections.emptyList(), cards);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        PaperParcelDeck.writeToParcel(this, dest, flags);
    }

    public static Deck of(List<Card> cards) {
        return new Deck(cards, Collections.emptyList());
    }

    private Stack<Card> toStack(Collection<Card> _this) {
        Stack<Card> stack = new Stack<>();
        List<Card> cards = new ArrayList<>(_this);
        Collections.reverse(cards);
        for(Card card : cards) {
            stack.push(card);
        }
        return stack;
    }

    private <T> List<T> append(T _this, List<T> collection) {
        List<T> list = new ArrayList<T>(collection.size() + 1);
        list.add(_this);
        list.addAll(collection);
        return list;
    }
}
