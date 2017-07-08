package com.taccardi.zak.library.model;

import com.taccardi.zak.library.pojo.Deck;

public abstract class BuildingDeckOperation {
    //when the deck is in the process of being built
    public static final class Building
            extends BuildingDeckOperation {
    }

    //an error happened while building the deck
    public static final class Error
            extends BuildingDeckOperation {
        private final String description;

        public Error(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    //successfully built deck
    public static final class Built
            extends BuildingDeckOperation {
        private final Deck deck;

        public Built(Deck deck) {
            this.deck = deck;
        }

        public Deck getDeck() {
            return deck;
        }
    }
}