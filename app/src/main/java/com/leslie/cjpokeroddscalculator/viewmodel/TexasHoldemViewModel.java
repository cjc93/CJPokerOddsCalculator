package com.leslie.cjpokeroddscalculator.viewmodel;

import com.leslie.cjpokeroddscalculator.calculation.TexasHoldemExactCalc;
import com.leslie.cjpokeroddscalculator.calculation.TexasHoldemMonteCarloCalc;
import com.leslie.cjpokeroddscalculator.outputresult.TexasHoldemFinalUpdate;
import com.leslie.cjpokeroddscalculator.outputresult.TexasHoldemLiveUpdate;

import java.util.Objects;

public class TexasHoldemViewModel extends EquityCalculatorViewModel {
    public Integer selectedRangePosition;

    @Override
    public double[] getInitialStats() {
        return new double[]{
            0.5,
            0.4797,
            0.0407,
            0.1741,
            0.4382,
            0.235,
            0.0483,
            0.0462,
            0.0303,
            0.026,
            0.0017,
            0.0003
        };
    }

    @Override
    public Thread createMonteCarloThread() {
        return new Thread(() -> {
            try {
                TexasHoldemMonteCarloCalc calcObj = new TexasHoldemMonteCarloCalc();
                calcObj.calculate(Objects.requireNonNull(boardCardRow.getValue()), playerCardRows.getValue(), new TexasHoldemLiveUpdate(this));
            } catch (InterruptedException ignored) { }
        });
    }

    @Override
    public Thread createExactCalcThread() {
        return new Thread(() -> {
            try {
                TexasHoldemExactCalc calcObj = new TexasHoldemExactCalc();
                calcObj.calculate(Objects.requireNonNull(boardCardRow.getValue()), playerCardRows.getValue(), new TexasHoldemFinalUpdate(this));
            } catch (InterruptedException ignored) { }
        });
    }
}
