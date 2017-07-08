package com.taccardi.zak.library.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import paperparcel.PaperParcel;

/**
 * A "card" in a deck. ex: Jack of clubs
 *
 * rank 2,3,4...Jack,Queen,etc
 * suit Hearts, Spades, etc
 */
@PaperParcel
public class Card
        implements Parcelable {
    public static final Creator<Card> CREATOR = PaperParcelCard.CREATOR;

    private final Rank rank;

    private final Suit suit;

    public Card(Rank rank, Suit suit) {
        this.rank = rank;
        this.suit = suit;
    }

    private final transient int id = hashCode();

    public int getId() {
        return id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        PaperParcelCard.writeToParcel(this, dest, flags);
    }

    public Rank getRank() {
        return rank;
    }

    public Suit getSuit() {
        return suit;
    }

    @Override
    public String toString() {
        return "Card{" + "rank=" + rank + ", suit=" + suit + '}';
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()) {
            return false;
        }

        Card card = (Card) o;

        if(rank != card.rank) {
            return false;
        }
        return suit == card.suit;

    }

    @Override
    public int hashCode() {
        int result = rank != null ? rank.hashCode() : 0;
        result = 31 * result + (suit != null ? suit.hashCode() : 0);
        return result;
    }
}
