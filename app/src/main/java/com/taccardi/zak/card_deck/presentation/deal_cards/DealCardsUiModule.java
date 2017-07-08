package com.taccardi.zak.card_deck.presentation.deal_cards;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jakewharton.rxrelay2.PublishRelay;
import com.jakewharton.rxrelay2.Relay;
import com.taccardi.zak.card_deck.R;
import com.taccardi.zak.card_deck.app.ActivityScope;
import com.taccardi.zak.library.model.Dealer;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public class DealCardsUiModule {
    private final DealCardsActivity activity;

    public DealCardsUiModule(DealCardsActivity activity) {
        this.activity = activity;
    }

    @Provides
    @ActivityScope
    @Named(DealCardsUiComponent.DEAL_CARD)
    Relay<Object> dealCardClicks() {
        return PublishRelay.create();
    }

    @Provides
    @ActivityScope
    @Named(DealCardsUiComponent.SHUFFLE_DECK)
    Relay<Object> shuffleDeckClicks() {
        return PublishRelay.create();
    }

    @Provides
    @ActivityScope
    @Named(DealCardsUiComponent.NEW_DECK)
    Relay<Object> newDeckRequests() {
        return PublishRelay.create();
    }

    @Provides
    @ActivityScope
    DealCardsUiRenderer renderer(DealCardsUi.Actions actions, com.taccardi.zak.library.pojo.Schedulers schedulers) {
        return new DealCardsUiRenderer(actions, schedulers.getMain(), schedulers.getComp());
    }

    @Provides
    @ActivityScope
    CardsRecycler cards(@Named(DealCardsUiComponent.DEAL_CARD) Relay<Object> relay) {
        return new CardsRecycler((RecyclerView) activity.findViewById(R.id.cards_recycler), relay);
    }

    @Provides
    @ActivityScope
    DealCardsPresenter presenter(DealCardsUi ui, DealCardsUi.Intentions intentions, Dealer dealer) {
        return new DealCardsPresenter(ui, intentions, dealer);
    }

    @Provides
    @ActivityScope
    @Named(DealCardsUiComponent.NEW_DECK)
    View newDeckButton() {
        return activity.findViewById(R.id.button_new_deck);
    }

    @Provides
    @ActivityScope
    @Named(DealCardsUiComponent.SHUFFLE_DECK)
    View shuffleButton() {
        return activity.findViewById(R.id.button_shuffle);
    }

    @Provides
    @ActivityScope
    @Named(DealCardsUiComponent.CARDS_LEFT)
    TextView cardsLeftHint() {
        return (TextView) activity.findViewById(R.id.dealCardsUi_cardsRemaining_textView);
    }

    @Provides
    @ActivityScope
    @Named(DealCardsUiComponent.PROGRESS_BAR)
    ProgressBar progressBar() {
        return (ProgressBar) activity.findViewById(R.id.dealCardsUi_progressBar_loading);
    }

    @Provides
    @ActivityScope
    @Named(DealCardsUiComponent.DEAL_CARDS_LAYOUT)
    ViewGroup dealCardsUiLayout() {
        return (ViewGroup) activity.findViewById(R.id.dealCardsUi);
    }

    @Provides
    @ActivityScope
    DealCardsUi dealCardsUi() {
        return activity;
    }

    @Provides
    @ActivityScope
    DealCardsUi.Actions dealCardsUiActions() {
        return activity;
    }

    @Provides
    @ActivityScope
    DealCardsUi.Intentions dealCardsUiIntentions() {
        return activity;
    }

    @Provides
    @ActivityScope
    @Named(DealCardsUiComponent.ERROR)
    TextView error() {
        return (TextView) activity.findViewById(R.id.dealCardsUi_error);
    }
}

//    @Provides @ActivityScope
//    fun main(): Scheduler {
//
//    }
//
//    @Provides @ActivityScope
//    fun disk(): Scheduler {
//
//    }
//
//    @Provides @ActivityScope
//    fun comp(): Scheduler {
//
//    }


//    class Dependencies(
//            val activity: DealCardsActivity,
//            val dealer: Dealer
//    ) : DealCardsUiComponent {
//
//        override fun injectMembers(p0: DealCardsActivity?) {
//            throw UnsupportedOperationException("not implemented")
//        }
//
//        override val dealCardClicks: Relay<Object> by lazy { PublishRelay.create<Unit>() }
//        override val shuffleDeckClicks: Relay<Object> by lazy { PublishRelay.create<Unit>() }
//
//        override val newDeckRequests: Relay<Object> by lazy { PublishRelay.create<Unit>() }
//        override val cards: CardsRecycler by lazy {
//            val recycler = activity.findViewById(R.id.cards_recycler) as RecyclerView
//            return@lazy CardsRecycler(recycler, dealCardClicks)
//        }
//        override val presenter by lazy {
//            DealCardsPresenter(activity, activity, activity, dealer)
//        }
//        override val cardsLeftHint: TextView by lazy {
//            activity.findViewById(R.id.dealCardsUi_cardsRemaining_textView) as TextView
//        }
//        override val shuffleButton: View by lazy {
//            activity.findViewById(R.id.button_shuffle)
//        }
//        override val newDeckButton: View by lazy {
//            activity.findViewById(R.id.button_new_deck)
//        }
//
//        override val progressBar: ProgressBar by lazy {
//            activity.findViewById(R.id.dealCardsUi_progressBar_loading) as ProgressBar
//        }
//
//        override val dealCardsUi: ViewGroup by lazy {
//            activity.findViewById(R.id.dealCardsUi) as ViewGroup
//        }
//
//        override val error: TextView by lazy {
//            activity.findViewById(R.id.dealCardsUi_error) as TextView
//        }
//
//        override val main: Scheduler by lazy { AndroidSchedulers.mainThread() }
//        override val disk: Scheduler by lazy { Schedulers.io() }
//        override val comp: Scheduler by lazy {
//            //            Schedulers.computation()
//            main
//        }
//
//        override val renderer by lazy { DealCardsUi.Renderer(activity, main = main, comp = comp) }
//
//    }