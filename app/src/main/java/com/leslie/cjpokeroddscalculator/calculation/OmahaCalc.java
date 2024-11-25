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
    public int totalSimulations;
    public OmahaPoker omahaPokerObj;

    public OmahaCalc(int cardsPerHand) {
        this.cardsPerHand = cardsPerHand;
    }

    public void setOmahaPokerObj(OmahaPoker omahaPokerObj) {
        this.omahaPokerObj = omahaPokerObj;
    }

    public String[][] convertPlayerCardsToStr(List<CardRow> cardRows) {
        String[][] playerCards = new String[cardRows.size() - 1][];

        for (int i = 1; i < cardRows.size(); i++) {
            playerCards[i - 1] = ((SpecificCardsRow) cardRows.get(i)).convertOmahaCardsToStr();
        }

        return playerCards;
    }

    public void calculate(List<CardRow> cardRows) throws InterruptedException {
        initialiseVariables(cardRows);

        String[] boardCards = ((SpecificCardsRow) cardRows.get(0)).convertOmahaCardsToStr();

        String[][] playerCards = convertPlayerCardsToStr(cardRows);

        String[] deck = Poker.remdeck(playerCards, boardCards);

        Cards cards = createCards(deck, boardCards, playerCards);

        try {
            this.totalSimulations = cards.count();
        } catch (ArithmeticException e) {
            throw new InterruptedException();
        }

        this.omahaPokerObj.omahaOutputResult.beforeAllSimulations();

        Equity[] eqs = this.omahaPokerObj.equityImpl(cards);

        this.omahaPokerObj.omahaOutputResult.afterAllSimulations(eqs);
    }

    public abstract Cards createCards(String[] deck, String[] boardCards, String[][] playerCards);
}
