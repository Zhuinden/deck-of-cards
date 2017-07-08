package com.taccardi.zak.library.model;

import com.taccardi.zak.library.pojo.Deck;

public abstract class ShuffleOperation {
    //when deck is in the process of being shuffled
    public static final class Shuffling
            extends ShuffleOperation {
    }

    //an error happened when shuffling the deck
    public static final class Error
            extends ShuffleOperation {
        private final String description;

        public Error(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    //successfully shuffled deck
    public static final class Shuffled
            extends ShuffleOperation {
        private final Deck deck;

        public Shuffled(Deck deck) {
            this.deck = deck;
        }

        public Deck getDeck() {
            return deck;
        }
    }

    ;
}



