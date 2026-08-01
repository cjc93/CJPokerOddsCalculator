package com.leslie.cjpokeroddscalculator.adapter;

import androidx.viewbinding.ViewBinding;

import com.leslie.cjpokeroddscalculator.util.AndroidStatic;
import com.leslie.cjpokeroddscalculator.cardrow.CardRow;
import com.leslie.cjpokeroddscalculator.cardrow.SpecificCardsRow;
import com.leslie.cjpokeroddscalculator.databinding.OmahaHighPlayerRowBinding;

import java.util.List;

public class OmahaHighPlayerViewHolder extends PlayerViewHolder {

    public OmahaHighPlayerViewHolder(ViewBinding binding, PlayerRowInteractionListener listener, int boardCardMaxHeight, int cardMaxWidth, int cardsPerHand) {
        super(binding.getRoot(), listener);

        OmahaHighPlayerRowBinding omahaHighBinding = (OmahaHighPlayerRowBinding) binding;
        omahaHighBinding.getRoot().setOnClickListener(v -> listener.onHideCardSelector());

        this.cardList = AndroidStatic.createOmahaCardButtons(omahaHighBinding, omahaHighBinding.getRoot(), omahaHighBinding.spaceAboveCards, omahaHighBinding.spaceBelowCards, cardsPerHand);

        this.removeButton = omahaHighBinding.remove;
        this.statsButton = omahaHighBinding.statsButton;
        this.statsView = omahaHighBinding.statsView.getRoot();

        initialiseViews(boardCardMaxHeight, cardMaxWidth);

        this.statsTextViewList = List.of(
            omahaHighBinding.equity,
            omahaHighBinding.win,
            omahaHighBinding.tie,
            omahaHighBinding.statsView.highCard,
            omahaHighBinding.statsView.onePair,
            omahaHighBinding.statsView.twoPair,
            omahaHighBinding.statsView.threeOfAKind,
            omahaHighBinding.statsView.straight,
            omahaHighBinding.statsView.flush,
            omahaHighBinding.statsView.fullHouse,
            omahaHighBinding.statsView.fourOfAKind,
            omahaHighBinding.statsView.straightFlush
        );
    }

    @Override
    public void bind(CardRow cardRow) {
        super.bind(cardRow);
        AndroidStatic.setCardRowImages(cardList, (SpecificCardsRow) cardRow);
    }
}