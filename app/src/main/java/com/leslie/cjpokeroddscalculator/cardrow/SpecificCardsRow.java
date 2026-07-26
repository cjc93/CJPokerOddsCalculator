package com.leslie.cjpokeroddscalculator.cardrow;

import com.leslie.cjpokeroddscalculator.GlobalStatic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

public class SpecificCardsRow extends CardRow {
    public String[] cards;

    public SpecificCardsRow(List<Double> stats, Boolean isStatsVisible, int numOfCards) {
        super(stats, isStatsVisible);

        cards = new String[numOfCards];

        for (int i = 0; i < numOfCards; i++) {
            cards[i] = "";
        }
    }

    @Override
    public void clear() {
        Arrays.fill(this.cards, "");
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

    @Override
    public CardRow copy() {
        SpecificCardsRow copy = new SpecificCardsRow(copyStats(), isStatsVisible, cards.length);
        copy.id = this.id;
        System.arraycopy(this.cards, 0, copy.cards, 0, this.cards.length);
        return copy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SpecificCardsRow that = (SpecificCardsRow) o;
        return Arrays.equals(cards, that.cards);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + Arrays.hashCode(cards);
        return result;
    }
}
