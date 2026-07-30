package com.leslie.cjpokeroddscalculator.adapter;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.View;

import androidx.constraintlayout.widget.Group;
import androidx.core.content.ContextCompat;
import androidx.viewbinding.ViewBinding;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;

import com.leslie.cjpokeroddscalculator.util.AndroidStatic;
import com.leslie.cjpokeroddscalculator.util.GlobalStatic;
import com.leslie.cjpokeroddscalculator.R;
import com.leslie.cjpokeroddscalculator.cardrow.CardRow;
import com.leslie.cjpokeroddscalculator.cardrow.RangeRow;
import com.leslie.cjpokeroddscalculator.cardrow.SpecificCardsRow;
import com.leslie.cjpokeroddscalculator.databinding.TexasHoldemPlayerRowBinding;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class TexasHoldemPlayerViewHolder extends PlayerViewHolder {
    public int rangeCardSize;
    public Group twoCardsGroup;
    public ShapeableImageView rangeButton;
    public MaterialButton handRangeButton;

    public TexasHoldemPlayerViewHolder(ViewBinding binding, PlayerRowInteractionListener listener, int boardCardMaxHeight, int cardMaxWidth, int rangeCardSize) {
        super(binding.getRoot(), listener);

        this.rangeCardSize = rangeCardSize;

        TexasHoldemPlayerRowBinding texasHoldemBinding = (TexasHoldemPlayerRowBinding) binding;
        texasHoldemBinding.getRoot().setOnClickListener(v -> listener.onHideCardSelector());

        this.cardList = Arrays.asList(texasHoldemBinding.card1, texasHoldemBinding.card2);

        this.playerText = texasHoldemBinding.playerText;
        this.removeButton = texasHoldemBinding.remove;
        this.statsButton = texasHoldemBinding.statsButton;
        this.statsView = texasHoldemBinding.statsView.getRoot();

        this.twoCardsGroup = texasHoldemBinding.twoCards;
        this.rangeButton = texasHoldemBinding.range;
        this.handRangeButton = texasHoldemBinding.handRangeButton;

        initialiseViews(boardCardMaxHeight, cardMaxWidth);

        this.statsTextViewList = List.of(
            texasHoldemBinding.equity,
            texasHoldemBinding.win,
            texasHoldemBinding.tie,
            texasHoldemBinding.statsView.highCard,
            texasHoldemBinding.statsView.onePair,
            texasHoldemBinding.statsView.twoPair,
            texasHoldemBinding.statsView.threeOfAKind,
            texasHoldemBinding.statsView.straight,
            texasHoldemBinding.statsView.flush,
            texasHoldemBinding.statsView.fullHouse,
            texasHoldemBinding.statsView.fourOfAKind,
            texasHoldemBinding.statsView.straightFlush
        );
    }

    @Override
    public void initialiseViews(int boardCardMaxHeight, int cardMaxWidth) {
        super.initialiseViews(boardCardMaxHeight, cardMaxWidth);
        rangeButton.setOnClickListener(v -> listener.onShowRangeSelector(getBindingAdapterPosition() + 1));
        handRangeButton.setOnClickListener(v -> listener.onToggleRangeHand(getBindingAdapterPosition() + 1));
    }

    @Override
    public void bind(CardRow cardRow) {
        super.bind(cardRow);

        if (cardRow instanceof SpecificCardsRow specificCardRow) {
            twoCardsGroup.setVisibility(View.VISIBLE);
            rangeButton.setVisibility(View.GONE);
            handRangeButton.setText(R.string.range);

            AndroidStatic.setCardRowImages(cardList, specificCardRow);
        } else {
            RangeRow rangeRow = (RangeRow) cardRow;
            twoCardsGroup.setVisibility(View.GONE);
            rangeButton.setVisibility(View.VISIBLE);
            handRangeButton.setText(R.string.hand);

            Bitmap matrixBitmap = Bitmap.createBitmap(13, 13, Bitmap.Config.ARGB_8888);
            for (int i = 0; i < 13; i++)  {
                for (int j = 0; j < 13; j++)  {
                    Set<String> suits = rangeRow.matrix.get(i).get(j);
                    if (GlobalStatic.isAllSuits(suits, i, j)) {
                        matrixBitmap.setPixel(j, i, ContextCompat.getColor(rangeButton.getContext(), R.color.all_suits));
                    } else if (suits.isEmpty()) {
                        matrixBitmap.setPixel(j, i, Color.DKGRAY);
                    } else {
                        matrixBitmap.setPixel(j, i, ContextCompat.getColor(rangeButton.getContext(), R.color.partial_suits));
                    }
                }
            }
            rangeButton.setImageBitmap(Bitmap.createScaledBitmap(matrixBitmap, rangeCardSize, rangeCardSize, false));
            matrixBitmap.recycle();
        }
    }
}