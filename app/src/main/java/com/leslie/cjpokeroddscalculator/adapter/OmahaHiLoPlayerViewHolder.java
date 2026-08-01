package com.leslie.cjpokeroddscalculator.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.viewbinding.ViewBinding;

import com.leslie.cjpokeroddscalculator.cardrow.CardRow;
import com.leslie.cjpokeroddscalculator.cardrow.SpecificCardsRow;
import com.leslie.cjpokeroddscalculator.util.AndroidStatic;
import com.leslie.cjpokeroddscalculator.R;
import com.leslie.cjpokeroddscalculator.databinding.OmahaHiloPlayerRowBinding;

import java.util.List;

public class OmahaHiLoPlayerViewHolder extends PlayerViewHolder {

    public OmahaHiLoPlayerViewHolder(ViewBinding binding, PlayerRowInteractionListener listener, int boardCardMaxHeight, int cardMaxWidth, int cardsPerHand) {
        super(binding.getRoot(), listener);

        OmahaHiloPlayerRowBinding omahaHiloBinding = (OmahaHiloPlayerRowBinding) binding;
        omahaHiloBinding.getRoot().setOnClickListener(v -> listener.onHideCardSelector());

        this.cardList = AndroidStatic.createOmahaCardButtons(omahaHiloBinding, omahaHiloBinding.getRoot(), omahaHiloBinding.spaceAboveCards, omahaHiloBinding.spaceBelowCards, cardsPerHand);

        this.removeButton = omahaHiloBinding.remove;
        this.statsButton = omahaHiloBinding.statsButton;
        this.statsView = omahaHiloBinding.statsView.getRoot();

        initialiseViews(boardCardMaxHeight, cardMaxWidth);

        TextView lowText = new TextView(omahaHiloBinding.getRoot().getContext(), null, 0, R.style.StatsText);
        lowText.setId(View.generateViewId());
        lowText.setText(R.string.low);

        TextView lowPercent = new TextView(omahaHiloBinding.getRoot().getContext(), null, 0, R.style.StatsText);
        lowPercent.setId(View.generateViewId());

        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
        );

        layoutParams.topToBottom = omahaHiloBinding.statsView.straightFlushText.getId();
        layoutParams.bottomToBottom = ConstraintSet.PARENT_ID;
        layoutParams.leftToRight = omahaHiloBinding.statsView.guideline3.getId();
        layoutParams.rightToLeft = lowPercent.getId();
        layoutParams.horizontalChainStyle = ConstraintLayout.LayoutParams.CHAIN_SPREAD_INSIDE;

        lowText.setLayoutParams(layoutParams);

        omahaHiloBinding.statsView.getRoot().addView(lowText);

        layoutParams = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
        );

        layoutParams.topToBottom = omahaHiloBinding.statsView.straightFlush.getId();
        layoutParams.bottomToBottom = ConstraintSet.PARENT_ID;
        layoutParams.leftToRight = lowText.getId();
        layoutParams.rightToLeft = omahaHiloBinding.statsView.guideline4.getId();
        layoutParams.horizontalChainStyle = ConstraintLayout.LayoutParams.CHAIN_SPREAD_INSIDE;

        lowPercent.setLayoutParams(layoutParams);

        omahaHiloBinding.statsView.getRoot().addView(lowPercent);

        this.statsTextViewList = List.of(
            omahaHiloBinding.equity,
            omahaHiloBinding.win,
            omahaHiloBinding.winLow,
            omahaHiloBinding.tie,
            omahaHiloBinding.tieLow,
            omahaHiloBinding.statsView.highCard,
            omahaHiloBinding.statsView.onePair,
            omahaHiloBinding.statsView.twoPair,
            omahaHiloBinding.statsView.threeOfAKind,
            omahaHiloBinding.statsView.straight,
            omahaHiloBinding.statsView.flush,
            omahaHiloBinding.statsView.fullHouse,
            omahaHiloBinding.statsView.fourOfAKind,
            omahaHiloBinding.statsView.straightFlush,
            lowPercent
        );
    }

    @Override
    public void bind(CardRow cardRow) {
        super.bind(cardRow);
        AndroidStatic.setCardRowImages(cardList, (SpecificCardsRow) cardRow);
    }
}