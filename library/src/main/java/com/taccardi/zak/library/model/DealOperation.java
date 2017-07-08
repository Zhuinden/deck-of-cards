package com.taccardi.zak.library.model;

import com.taccardi.zak.library.pojo.Card;

public abstract class DealOperation {
    //when card is in the process of being dealt
    public static final class Dealing
            extends DealOperation {
    }

    //an error happened when dealing the card
    public static final class Error
            extends DealOperation {
        private final String description;

        public Error(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    //successfully dealt card
    public static final class TopCard
            extends DealOperation {
        private final Card card;

        public TopCard(Card card) {
            this.card = card;
        }

        public Card getCard() {
            return card;
        }
    }
}