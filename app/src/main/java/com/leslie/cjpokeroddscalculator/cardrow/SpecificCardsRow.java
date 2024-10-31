package com.leslie.cjpokeroddscalculator.cardrow;

import com.leslie.cjpokeroddscalculator.GlobalStatic;
import com.leslie.cjpokeroddscalculator.fragment.EquityCalculatorFragment;

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

    @Override
    public void clear(EquityCalculatorFragment equityCalculatorFragment, int rowIdx) {
        Arrays.fill(this.cards, "");

        for (int i = 0; i < this.cards.length; i++) {
            equityCalculatorFragment.setCardImage(rowIdx, i, "");
        }
    }

    @Override
    public boolean isKnownPlayer() {
        for (String card : this.cards) {
            if (!Objects.equals(card, "")) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String convertTexasHoldemPlayerCardsToStr() {
        StringBuilder temp = new StringBuilder();

        temp.append(this.cards[0]);
        temp.append(this.cards[1]);
        if (String.valueOf(temp).isEmpty()) {
            return "random";
        } else if (temp.length() == 2) {
            StringJoiner sj = new StringJoiner(",");
            for (String card : GlobalStatic.allPossibleCards) {
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
            if (!card.isEmpty()) {
                omahaCards.add(card);
            }
        }
        return omahaCards.toArray(new String[0]);
    }
}
