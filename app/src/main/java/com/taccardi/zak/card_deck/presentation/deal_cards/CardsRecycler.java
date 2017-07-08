package com.taccardi.zak.card_deck.presentation.deal_cards;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jakewharton.rxrelay2.Relay;
import com.taccardi.zak.card_deck.R;
import com.taccardi.zak.card_deck.presentation.deal_cards.CardsRecycler.Item.UiCard;
import com.taccardi.zak.card_deck.presentation.deal_cards.CardsRecycler.Item.UiDeck;
import com.taccardi.zak.library.pojo.Card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Delegate for the recyclerview in [DealCardsUi] that displays the cards that were dealt.
 *
 * @property recyclerView the view itself
 * @property deckClicks relay that should emit when the user clicks on the deck (to deal a card)
 */
public class CardsRecycler {
    private final RecyclerView recyclerView;
    private final Relay<Object> deckClicks;
    private Adapter adapter;

    public CardsRecycler(RecyclerView recyclerView, Relay<Object> deckClicks) {
        this.recyclerView = recyclerView;
        this.deckClicks = deckClicks;
        adapter = new Adapter(deckClicks);
        recyclerView.setLayoutManager(new NoScrollLinearLayoutManager(recyclerView.getContext(),
                RecyclerView.HORIZONTAL,
                false));
        recyclerView.setAdapter(adapter);
    }

    public void showCardsDealt(List<? extends Item> cards) {
        adapter.showCardsDealt(cards);
    }

    private static class NoScrollLinearLayoutManager
            extends LinearLayoutManager {
        public NoScrollLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
        }

        @Override
        public boolean canScrollHorizontally() {
            return false;
        }

        @Override
        public boolean canScrollVertically() {
            return false;
        }
    }


    private static class Adapter
            extends RecyclerView.Adapter<UiViewHolder<Item>> {
        private final Relay<Object> deckClicks;

        public Adapter(Relay<Object> deckClicks) {
            this.deckClicks = deckClicks;
        }

        private List<Item> items = Collections.emptyList();

        @Override
        public int getItemCount() {
            return items.size();
        }

        @Override
        public void onBindViewHolder(UiViewHolder<Item> holder, int position) {
            holder.bind(items.get(position));
        }

        @Override
        public UiViewHolder<Item> onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            UiViewHolder.ViewType type = UiViewHolder.ViewType.of(viewType);
            View view = inflater.inflate(type.layoutId, parent, false);
            return UiViewHolder.create(view, type, deckClicks);
        }


        @Override
        public int getItemViewType(int position) {
            return items.get(position).getViewType();
        }

        public void showCardsDealt(List<? extends Item> cards) {
            this.items = new ArrayList<>(cards);
            notifyDataSetChanged();
        }

        public void showDeck(DealCardsUi.RecyclerViewBinding<Item> diff) {
            this.items = diff.getNew();
            diff.getDiff().dispatchUpdatesTo(this);
        }
    }

    private static class CardItemAnimator
            extends DefaultItemAnimator {
        @Override
        public boolean canReuseUpdatedViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, @NonNull List<Object> payloads) {
            if(viewHolder instanceof UiViewHolder.DeckHolder) {
                return true;
            }
            return super.canReuseUpdatedViewHolder(viewHolder, payloads);
        }
    }

    private static abstract class UiViewHolder<I>
            extends RecyclerView.ViewHolder {
        public UiViewHolder(View itemView) {
            super(itemView);
        }

        public abstract void bind(I item);

        /**
         * View holder for cards
         */
        static class CardHolder
                extends UiViewHolder<UiCard> {
            public CardHolder(View itemView) {
                super(itemView);
            }

            @Override
            public void bind(UiCard item) {
                // TODO replace with butterknife in constructor
                TextView bottomRight = (TextView) itemView.findViewById(R.id.dealCardsUi_bottomRight_suitRank);
                TextView topLeft = (TextView) itemView.findViewById(R.id.dealCardsUi_topLeft_suitRank);

                Spanned cardHtml = Html.fromHtml(item.getCard().getRank() + "<br /> + " + item.getCard()
                        .getSuit()
                        .getSymbol());
                bottomRight.setText(cardHtml);
                topLeft.setText(cardHtml);

                TextView centerSuit = (TextView) itemView.findViewById(R.id.dealCardsUi_center_suit);
                centerSuit.setText(Html.fromHtml(item.getCard().getSuit().getSymbol())); // TODO is this right?
            }
        }

        static class DeckHolder
                extends UiViewHolder<UiDeck> {
            private final Object UNIT = new Object();
            private final Relay<Object> deckClicks;

            public DeckHolder(View itemView, Relay<Object> deckClicks) {
                super(itemView);
                this.deckClicks = deckClicks;
                itemView.setOnClickListener(v -> deckClicks.accept(UNIT));
            }

            @Override
            public void bind(UiDeck item) {
                // no bind needed
            }
        }

        @SuppressWarnings("unchecked")
        public static <T extends Item> UiViewHolder<T> create(View itemView, ViewType viewType, Relay<Object> deckClicks) {
            if(viewType == ViewType.CARD) {
                return (UiViewHolder<T>) new CardHolder(itemView);
            } else if(viewType == ViewType.DECK) {
                return (UiViewHolder<T>) new DeckHolder(itemView, deckClicks);
            } else {
                throw new IllegalArgumentException("View Type [" + viewType + "] does not exist.");
            }
        }

        public enum ViewType {
            CARD(R.layout.item_deal_cards_ui_card),
            DECK(R.layout.item_deal_cards_ui_deck);

            private final int layoutId;

            private ViewType(@LayoutRes final int layoutId) {
                this.layoutId = layoutId;
            }

            public static final ViewType[] VALUES = ViewType.values();

            public static ViewType of(@LayoutRes int layoutId) {
                for(ViewType viewType : VALUES) {
                    if(viewType.layoutId == layoutId) {
                        return viewType;
                    }
                }
                throw new EnumConstantNotPresentException(ViewType.class,
                        "could not find view type for [" + layoutId + "]");
            }
        }
    }

    /**
     * An item in the [CardsRecycler]View.
     */
    public static abstract class Item {
        public abstract int getViewType();

        public abstract int getLayoutId();

        public abstract boolean isItemSame(Item newItem);

        public abstract boolean isContentSame(Item newItem);

        /**
         * The UI representation of a [Card]
         */
        public static final class UiCard
                extends Item {
            private final Card card;

            public UiCard(Card card) {
                this.card = card;
            }

            public Card getCard() {
                return card;
            }

            @Override
            public int getViewType() {
                return getLayoutId();
            }

            @Override
            public int getLayoutId() {
                return R.layout.item_deal_cards_ui_card;
            }

            @Override
            public boolean isItemSame(Item newItem) {
                return newItem instanceof UiCard && newItem == this;
            }

            @Override
            public boolean isContentSame(Item newItem) {
                return newItem instanceof UiCard && this.equals(newItem);
            }
        }

        /**
         * The UI representation of a [Deck].
         */
        public static final class UiDeck
                extends Item {
            @Override
            public int getViewType() {
                return getLayoutId();
            }

            @Override
            public int getLayoutId() {
                return R.layout.item_deal_cards_ui_deck;
            }

            @Override
            public boolean isItemSame(Item newItem) {
                return newItem instanceof UiDeck; // deck is always same
            }

            @Override
            public boolean isContentSame(Item newItem) {
                return newItem instanceof UiDeck; // deck is always same
            }
        }
    }

    public void showDeck(DealCardsUi.RecyclerViewBinding<Item> diff) {
        this.adapter.showDeck(diff);
    }
}


