package com.leslie.cjpokeroddscalculator.calculation;

import com.leslie.cjpokeroddscalculator.cardrow.CardRow;
import com.leslie.cjpokeroddscalculator.outputresult.TexasHoldemOutputResult;

import java.util.List;

public class TexasHoldemCalc extends Calculation{
    static {
        System.loadLibrary("cjpokeroddscalculator");
    }

    public TexasHoldemOutputResult outputResultObj;

    public void initialiseVariables(List<CardRow> cardRows, TexasHoldemOutputResult outputResultObj) {
        super.initialiseVariables(cardRows);
        this.outputResultObj = outputResultObj;
    }

    public String convertBoardCardsToStr(String[] cards) {
        StringBuilder boardCards = new StringBuilder();
        for (String card : cards) {
            boardCards.append(card);
        }
        return String.valueOf(boardCards);
    }

    public String[] convertPlayerCardsToStr(List<CardRow> cardRows) {
        String[] playerCards = new String[cardRows.size() - 1];

        for (int i = 1; i < cardRows.size(); i++) {
            playerCards[i - 1] = cardRows.get(i).convertTexasHoldemPlayerCardsToStr();
        }

        return playerCards;
    }

    public double[][] averageUnknownStats(double[] equity, double[] win, double[] highCard, double[] onePair, double[] twoPair, double[] threeOfAKind, double[] straight, double[] flush, double[] fullHouse, double[] fourOfAKind, double[] straightFlush) {
        double unknownPlayersEquity = 0;
        double unknownPlayersWin = 0;

        double unknownPlayersHighCard = 0;
        double unknownPlayersOnePair = 0;
        double unknownPlayersTwoPair = 0;
        double unknownPlayersThreeOfAKind = 0;
        double unknownPlayersStraight = 0;
        double unknownPlayersFlush = 0;
        double unknownPlayersFullHouse = 0;
        double unknownPlayersFourOfAKind = 0;
        double unknownPlayersStraightFlush = 0;

        for(int i = 0; i < this.knownPlayers.length; i++) {
            if(!knownPlayers[i]) {
                unknownPlayersEquity += equity[i];
                unknownPlayersWin += win[i];

                unknownPlayersHighCard += highCard[i];
                unknownPlayersOnePair += onePair[i];
                unknownPlayersTwoPair += twoPair[i];
                unknownPlayersThreeOfAKind += threeOfAKind[i];
                unknownPlayersStraight += straight[i];
                unknownPlayersFlush += flush[i];
                unknownPlayersFullHouse += fullHouse[i];
                unknownPlayersFourOfAKind += fourOfAKind[i];
                unknownPlayersStraightFlush += straightFlush[i];
            }
        }

        unknownPlayersEquity = unknownPlayersEquity / this.numOfUnknownPlayers;
        unknownPlayersWin = unknownPlayersWin / this.numOfUnknownPlayers;

        unknownPlayersHighCard = unknownPlayersHighCard / this.numOfUnknownPlayers;
        unknownPlayersOnePair = unknownPlayersOnePair / this.numOfUnknownPlayers;
        unknownPlayersTwoPair = unknownPlayersTwoPair / this.numOfUnknownPlayers;
        unknownPlayersThreeOfAKind = unknownPlayersThreeOfAKind / this.numOfUnknownPlayers;
        unknownPlayersStraight = unknownPlayersStraight / this.numOfUnknownPlayers;
        unknownPlayersFlush = unknownPlayersFlush / this.numOfUnknownPlayers;
        unknownPlayersFullHouse = unknownPlayersFullHouse / this.numOfUnknownPlayers;
        unknownPlayersFourOfAKind = unknownPlayersFourOfAKind / this.numOfUnknownPlayers;
        unknownPlayersStraightFlush = unknownPlayersStraightFlush / this.numOfUnknownPlayers;

        for(int i = 0; i < knownPlayers.length; i++) {
            if(!knownPlayers[i]) {
                equity[i] = unknownPlayersEquity;
                win[i] = unknownPlayersWin;

                highCard[i] = unknownPlayersHighCard;
                onePair[i] = unknownPlayersOnePair;
                twoPair[i] = unknownPlayersTwoPair;
                threeOfAKind[i] = unknownPlayersThreeOfAKind;
                straight[i] = unknownPlayersStraight;
                flush[i] = unknownPlayersFlush;
                fullHouse[i] = unknownPlayersFullHouse;
                fourOfAKind[i] = unknownPlayersFourOfAKind;
                straightFlush[i] = unknownPlayersStraightFlush;
            }
        }

        return new double[][] {equity, win, highCard, onePair, twoPair, threeOfAKind, straight, flush, fullHouse, fourOfAKind, straightFlush};
    }
}
