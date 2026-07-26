package com.leslie.cjpokeroddscalculator.adapter;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageButton;

import com.leslie.cjpokeroddscalculator.GlobalStatic;
import com.leslie.cjpokeroddscalculator.R;
import com.leslie.cjpokeroddscalculator.cardrow.CardRow;
import com.leslie.cjpokeroddscalculator.cardrow.RangeRow;
import com.leslie.cjpokeroddscalculator.cardrow.SpecificCardsRow;
import com.leslie.cjpokeroddscalculator.databinding.TexasHoldemPlayerRowBinding;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class TexasHoldemPlayerViewHolder extends PlayerViewHolder {
    private final TexasHoldemPlayerRowBinding binding;
    private final int rangeCardSize;

    public TexasHoldemPlayerViewHolder(TexasHoldemPlayerRowBinding binding, PlayerRowInteractionListener listener, int boardCardMaxHeight, int cardMaxWidth, int rangeCardSize) {
        super(binding.getRoot(), listener, boardCardMaxHeight, cardMaxWidth);
        this.binding = binding;
        this.rangeCardSize = rangeCardSize;
    }

    @Override
    public void bind(CardRow cardRow, int rowIdx, int[] selectedCard) {
        binding.playerText.setText(binding.getRoot().getContext().getString(R.string.player, rowIdx));

        binding.remove.setOnClickListener(v -> listener.onRemovePlayer(rowIdx));
        binding.statsButton.setOnClickListener(v -> listener.onToggleStats(rowIdx));

        binding.statsView.getRoot().setVisibility(cardRow.isStatsVisible ? View.VISIBLE : View.GONE);

        if (cardRow.stats != null) {
            binding.equity.setText(binding.getRoot().getContext().getString(R.string.two_decimal_perc, cardRow.stats.get(0) * 100));
            binding.win.setText(binding.getRoot().getContext().getString(R.string.two_decimal_perc, cardRow.stats.get(1) * 100));
            binding.tie.setText(binding.getRoot().getContext().getString(R.string.two_decimal_perc, cardRow.stats.get(2) * 100));

            binding.statsView.highCard.setText(binding.getRoot().getContext().getString(R.string.two_decimal_perc, cardRow.stats.get(3) * 100));
            binding.statsView.onePair.setText(binding.getRoot().getContext().getString(R.string.two_decimal_perc, cardRow.stats.get(4) * 100));
            binding.statsView.twoPair.setText(binding.getRoot().getContext().getString(R.string.two_decimal_perc, cardRow.stats.get(5) * 100));
            binding.statsView.threeOfAKind.setText(binding.getRoot().getContext().getString(R.string.two_decimal_perc, cardRow.stats.get(6) * 100));
            binding.statsView.straight.setText(binding.getRoot().getContext().getString(R.string.two_decimal_perc, cardRow.stats.get(7) * 100));
            binding.statsView.flush.setText(binding.getRoot().getContext().getString(R.string.two_decimal_perc, cardRow.stats.get(8) * 100));
            binding.statsView.fullHouse.setText(binding.getRoot().getContext().getString(R.string.two_decimal_perc, cardRow.stats.get(9) * 100));
            binding.statsView.fourOfAKind.setText(binding.getRoot().getContext().getString(R.string.two_decimal_perc, cardRow.stats.get(10) * 100));
            binding.statsView.straightFlush.setText(binding.getRoot().getContext().getString(R.string.two_decimal_perc, cardRow.stats.get(11) * 100));
        } else {
            binding.equity.setText("");
            binding.win.setText("");
            binding.tie.setText("");
            binding.statsView.highCard.setText("");
            binding.statsView.onePair.setText("");
            binding.statsView.twoPair.setText("");
            binding.statsView.threeOfAKind.setText("");
            binding.statsView.straight.setText("");
            binding.statsView.flush.setText("");
            binding.statsView.fullHouse.setText("");
            binding.statsView.fourOfAKind.setText("");
            binding.statsView.straightFlush.setText("");
        }

        if (cardRow instanceof SpecificCardsRow specificCardRow) {
            binding.twoCards.setVisibility(View.VISIBLE);
            binding.range.setVisibility(View.GONE);
            binding.handRangeButton.setText(R.string.range);

            List<ImageButton> cardList = Arrays.asList(binding.card1, binding.card2);
            for (int i = 0; i < cardList.size(); i++) {
                cardList.get(i).setMaxHeight(boardCardMaxHeight);
                cardList.get(i).setMaxWidth(cardMaxWidth);
                int cardIdx = i;
                cardList.get(i).setOnClickListener(v -> listener.onSelectCard(rowIdx, cardIdx));
            }

            for (int i = 0; i < specificCardRow.cards.length; i++) {
                String cardStr = specificCardRow.cards[i];
                GlobalStatic.setCardImage(cardList.get(i), cardStr);

                if (selectedCard != null && selectedCard[0] == rowIdx && selectedCard[1] == i) {
                    cardList.get(i).setBackgroundResource(R.drawable.selected_border);
                } else {
                    cardList.get(i).setBackgroundResource(0);
                }
            }
        } else {
            RangeRow rangeRow = (RangeRow) cardRow;
            binding.twoCards.setVisibility(View.GONE);
            binding.range.setVisibility(View.VISIBLE);
            binding.handRangeButton.setText(R.string.hand);

            Bitmap matrixBitmap = Bitmap.createBitmap(13, 13, Bitmap.Config.ARGB_8888);
            for (int i = 0; i < 13; i++)  {
                for (int j = 0; j < 13; j++)  {
                    Set<String> suits = rangeRow.matrix.get(i).get(j);
                    if (GlobalStatic.isAllSuits(suits, i, j)) {
                        matrixBitmap.setPixel(j, i, Color.YELLOW);
                    } else if (suits.isEmpty()) {
                        matrixBitmap.setPixel(j, i, Color.LTGRAY);
                    } else {
                        matrixBitmap.setPixel(j, i, Color.CYAN);
                    }
                }
            }
            binding.range.setImageBitmap(Bitmap.createScaledBitmap(matrixBitmap, rangeCardSize, rangeCardSize, false));
            matrixBitmap.recycle();
        }

        binding.handRangeButton.setOnClickListener(v -> listener.onToggleRangeHand(rowIdx));
        binding.range.setOnClickListener(v -> listener.onShowRangeSelector(rowIdx));
        binding.getRoot().setOnClickListener(v -> listener.onHideCardSelector());
    }
}