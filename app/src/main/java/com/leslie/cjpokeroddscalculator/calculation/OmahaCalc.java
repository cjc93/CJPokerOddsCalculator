package com.leslie.cjpokeroddscalculator.calculation;

import com.leslie.cjpokeroddscalculator.calculation.pet.Cards;
import com.leslie.cjpokeroddscalculator.calculation.pet.OmahaPoker;
import com.leslie.cjpokeroddscalculator.calculation.pet.Poker;
import com.leslie.cjpokeroddscalculator.cardrow.CardRow;
import com.leslie.cjpokeroddscalculator.cardrow.SpecificCardsRow;
import com.leslie.cjpokeroddscalculator.calculation.pet.Equity;
import com.leslie.cjpokeroddscalculator.outputresult.OmahaOutputResult;

import java.util.List;

public abstract class OmahaCalc extends Calculation{
    public final int cardsPerHand;
    public OmahaPoker omahaPokerObj;

    public OmahaCalc(OmahaPoker omahaPoker, int cardsPerHand) {
        this.omahaPokerObj = omahaPoker;
        this.cardsPerHand = cardsPerHand;
    }

    public String[][] convertPlayerCardsToStr(List<CardRow> cardRows) {
        String[][] playerCards = new String[cardRows.size() - 1][];

        for (int i = 1; i < cardRows.size(); i++) {
            playerCards[i - 1] = ((SpecificCardsRow) cardRows.get(i)).convertOmahaCardsToStr();
        }

        return playerCards;
    }

    public void calculate(List<CardRow> cardRows, OmahaOutputResult omahaOutputResult) throws InterruptedException {
        initialiseVariables(cardRows);

        String[] boardCards = ((SpecificCardsRow) cardRows.get(0)).convertOmahaCardsToStr();

        String[][] playerCards = convertPlayerCardsToStr(cardRows);

        String[] deck = Poker.remdeck(playerCards, boardCards);

        Cards cards = createCards(deck, boardCards, playerCards);

        int totalSimulations;
        try {
            totalSimulations = cards.count();
        } catch (ArithmeticException e) {
            throw new InterruptedException();
        }

        this.omahaPokerObj.omahaOutputResult = omahaOutputResult;
        this.omahaPokerObj.omahaOutputResult.totalSimulations = totalSimulations;
        this.omahaPokerObj.omahaOutputResult.averageUnknownStats = this::averageUnknownStats;

        this.omahaPokerObj.omahaOutputResult.beforeAllSimulations();

        Equity[] eqs = this.omahaPokerObj.equityImpl(cards);

        this.omahaPokerObj.omahaOutputResult.afterAllSimulations(eqs);
    }

    public abstract Cards createCards(String[] deck, String[] boardCards, String[][] playerCards);
}
