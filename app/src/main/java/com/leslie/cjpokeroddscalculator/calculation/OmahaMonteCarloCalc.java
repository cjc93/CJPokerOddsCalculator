package com.leslie.cjpokeroddscalculator.calculation;

import com.leslie.cjpokeroddscalculator.cardrow.CardRow;
import com.leslie.cjpokeroddscalculator.calculation.pet.Cards;
import com.leslie.cjpokeroddscalculator.outputresult.OmahaOutputResult;
import com.leslie.cjpokeroddscalculator.cardrow.SpecificCardsRow;
import com.leslie.cjpokeroddscalculator.calculation.pet.CardsMonteCarlo;
import com.leslie.cjpokeroddscalculator.calculation.pet.Equity;
import com.leslie.cjpokeroddscalculator.calculation.pet.OmahaPoker;
import com.leslie.cjpokeroddscalculator.calculation.pet.Poker;

import java.util.List;

public class OmahaMonteCarloCalc extends OmahaCalc {

    public void calculate(List<CardRow> cardRows, int playersRemainingNo, OmahaOutputResult outputResultObj) throws InterruptedException {
        initialiseVariables(cardRows, playersRemainingNo, outputResultObj);

        OmahaPoker poker = new OmahaPoker(outputResultObj);

        String[] boardCards = ((SpecificCardsRow) cardRows.get(0)).convertOmahaCardsToStr();

        String[][] playerCards = convertPlayerCardsToStr(cardRows, playersRemainingNo);

        String[] deck = Poker.remdeck(playerCards, boardCards);

        Cards cards = new CardsMonteCarlo(deck, boardCards, playerCards, 2000000000);
        this.totalSimulations = cards.count();

        outputResultObj.beforeAllSimulations();

        Equity[] eqs = poker.equityImpl(cards);

        outputResultObj.afterAllSimulations(eqs);
    }
}
