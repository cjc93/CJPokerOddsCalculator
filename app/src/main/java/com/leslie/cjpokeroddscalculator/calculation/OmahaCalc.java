package com.leslie.cjpokeroddscalculator.calculation;

import com.leslie.cjpokeroddscalculator.calculation.pet.Cards;
import com.leslie.cjpokeroddscalculator.calculation.pet.OmahaPoker;
import com.leslie.cjpokeroddscalculator.calculation.pet.Poker;
import com.leslie.cjpokeroddscalculator.cardrow.CardRow;
import com.leslie.cjpokeroddscalculator.cardrow.SpecificCardsRow;
import com.leslie.cjpokeroddscalculator.calculation.pet.Equity;

import java.util.List;

public abstract class OmahaCalc extends Calculation{
    public final int cardsPerHand;
    public OmahaPoker omahaPokerObj;

    public OmahaCalc(OmahaPoker omahaPoker, int cardsPerHand) {
        this.omahaPokerObj = omahaPoker;
        this.cardsPerHand = cardsPerHand;
    }

    public String[][] convertPlayerCardsToStr(List<CardRow> playerCardRows) {
        String[][] playerCards = new String[playerCardRows.size()][];

        for (int i = 0; i < playerCardRows.size(); i++) {
            playerCards[i] = ((SpecificCardsRow) playerCardRows.get(i)).convertOmahaCardsToStr();
        }

        return playerCards;
    }

    public void calculate(SpecificCardsRow boardCardRow, List<CardRow> playerCardRows, OmahaProgressListener omahaProgressListener) throws InterruptedException {
        initialiseVariables(playerCardRows);

        String[] boardCards = boardCardRow.convertOmahaCardsToStr();

        String[][] playerCards = convertPlayerCardsToStr(playerCardRows);

        String[] deck = Poker.remdeck(playerCards, boardCards);

        Cards cards = createCards(deck, boardCards, playerCards);

        int totalSimulations;
        try {
            totalSimulations = cards.count();
        } catch (ArithmeticException e) {
            throw new InterruptedException();
        }

        this.omahaPokerObj.omahaProgressListener = omahaProgressListener;
        this.omahaPokerObj.omahaProgressListener.setTotalSimulations(totalSimulations);
        this.omahaPokerObj.omahaProgressListener.setAverageUnknownStats(this::averageUnknownStats);

        this.omahaPokerObj.omahaProgressListener.beforeAllSimulations();

        Equity[] eqs = this.omahaPokerObj.equityImpl(cards);

        this.omahaPokerObj.omahaProgressListener.afterAllSimulations(eqs);
    }

    public abstract Cards createCards(String[] deck, String[] boardCards, String[][] playerCards);
}
