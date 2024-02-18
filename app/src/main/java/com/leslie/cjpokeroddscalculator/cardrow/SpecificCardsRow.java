package com.leslie.cjpokeroddscalculator.cardrow;

import android.graphics.drawable.Drawable;
import android.view.View;

import com.leslie.cjpokeroddscalculator.GlobalStatic;
import com.leslie.cjpokeroddscalculator.fragment.EquityCalculatorFragment;
import com.leslie.cjpokeroddscalculator.fragment.TexasHoldemFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

public class SpecificCardsRow extends CardRow {
    public int[][] cards;

    public SpecificCardsRow(int numOfCards) {
        cards = new int[numOfCards][];

        for (int i = 0; i < numOfCards; i++) {
            cards[i] = new int[]{0, 0};
        }
    }

    public SpecificCardsRow(SpecificCardsRow other) {
        this.cards = new int[other.cards.length][];
        for (int i = 0; i < other.cards.length; i++) {
            this.cards[i] = Arrays.copyOf(other.cards[i], other.cards[i].length);
        }
    }

    public void clear(EquityCalculatorFragment equityCalculatorFragment, int row_idx) {
        for (int[] card : this.cards) {
            Arrays.fill(card, 0);
        }

        for (int i = 0; i < this.cards.length; i++) {
            equityCalculatorFragment.setCardImage(row_idx, i, 0, 0);
        }
    }

    public CardRow copy() {
        return new SpecificCardsRow(this);
    }

    public void copyImageBelow(EquityCalculatorFragment equityCalculatorFragment, int row_idx) {
        for (int i = 0; i < this.cards.length; i++) {
            Drawable d = Objects.requireNonNull(equityCalculatorFragment.cardPositionBiMap.get(Arrays.asList(row_idx + 1, i))).getDrawable();
            Objects.requireNonNull(equityCalculatorFragment.cardPositionBiMap.get(Arrays.asList(row_idx, i))).setImageDrawable(d);
        }

        if (equityCalculatorFragment instanceof TexasHoldemFragment) {
            TexasHoldemFragment texasHoldemFragment = (TexasHoldemFragment) equityCalculatorFragment;
            Objects.requireNonNull(texasHoldemFragment.rangePositionBiMap.get(row_idx)).setVisibility(View.GONE);
            texasHoldemFragment.twoCardsLayouts[row_idx - 1].setVisibility(View.VISIBLE);
        }
    }

    public boolean isKnownPlayer() {
        for (int[] card : this.cards) {
            if (card[0] != 0) {
                return true;
            }
        }

        return false;
    }

    public String convertTexasHoldemPlayerCardsToStr() {
        StringBuilder temp = new StringBuilder();

        temp.append(GlobalStatic.rankToStr.get(this.cards[0][1]));
        temp.append(GlobalStatic.suitToStr.get(this.cards[0][0]));
        temp.append(GlobalStatic.rankToStr.get(this.cards[1][1]));
        temp.append(GlobalStatic.suitToStr.get(this.cards[1][0]));
        if (String.valueOf(temp).equals("")) {
            return "random";
        } else if (temp.length() == 2) {
            StringJoiner sj = new StringJoiner(",");
            for (String card : GlobalStatic.all_possible_cards) {
                if (!card.equals(String.valueOf(temp))) {
                    sj.add(temp + card);
                }
            }
            return sj.toString();
        } else {
            return String.valueOf(temp);
        }
    }

    public String[] convertOmahaCardsToStr() {
        List<String> omahaCards = new ArrayList<>();
        for (int[] card : cards) {
            String cardStr = GlobalStatic.rankToStr.get(card[1]) + GlobalStatic.suitToStr.get(card[0]);
            if (!cardStr.equals("")) {
                omahaCards.add(cardStr);
            }
        }
        return omahaCards.toArray(new String[0]);
    }
}
