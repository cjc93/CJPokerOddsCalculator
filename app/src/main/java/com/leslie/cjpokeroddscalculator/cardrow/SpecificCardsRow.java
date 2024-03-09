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
    public String[] cards;

    public SpecificCardsRow(int numOfCards) {
        cards = new String[numOfCards];

        for (int i = 0; i < numOfCards; i++) {
            cards[i] = "";
        }
    }

    public SpecificCardsRow(SpecificCardsRow other) {
        this.cards = new String[other.cards.length];
        System.arraycopy(other.cards, 0, this.cards, 0, other.cards.length);
    }

    public void clear(EquityCalculatorFragment equityCalculatorFragment, int row_idx) {
        Arrays.fill(this.cards, "");

        for (int i = 0; i < this.cards.length; i++) {
            equityCalculatorFragment.setCardImage(row_idx, i, "");
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
            texasHoldemFragment.twoCardsGroups.get(row_idx - 1).setVisibility(View.VISIBLE);
        }
    }

    public boolean isKnownPlayer() {
        for (String card : this.cards) {
            if (!Objects.equals(card, "")) {
                return true;
            }
        }

        return false;
    }

    public String convertTexasHoldemPlayerCardsToStr() {
        StringBuilder temp = new StringBuilder();

        temp.append(this.cards[0]);
        temp.append(this.cards[1]);
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
        for (String card : cards) {
            if (!card.equals("")) {
                omahaCards.add(card);
            }
        }
        return omahaCards.toArray(new String[0]);
    }
}
