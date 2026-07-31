package com.leslie.cjpokeroddscalculator.viewmodel;

import com.leslie.cjpokeroddscalculator.calculation.OmahaExactCalc;
import com.leslie.cjpokeroddscalculator.calculation.OmahaMonteCarloCalc;
import com.leslie.cjpokeroddscalculator.calculation.pet.OmahaHiLoPoker;
import com.leslie.cjpokeroddscalculator.outputresult.OmahaFinalUpdate;
import com.leslie.cjpokeroddscalculator.outputresult.OmahaLiveUpdate;

import java.util.Objects;

public class OmahaHiLoViewModel extends OmahaHighViewModel {
    @Override
    public double[] getInitialStats() {
        if (cardsPerHand == 5) {
            return new double[]{
                0.5,
                0.4898,
                0.2644,
                0.0203,
                0.0263,
                0.0078,
                0.1668,
                0.3762,
                0.0965,
                0.1525,
                0.0961,
                0.0956,
                0.0072,
                0.0014,
                0.4302
            };
        } else if (cardsPerHand == 6) {
            return new double[]{
                0.5,
                0.4862,
                0.2725,
                0.0275,
                0.0372,
                0.0014,
                0.0979,
                0.3485,
                0.1027,
                0.1855,
                0.1225,
                0.1292,
                0.0101,
                0.0022,
                0.4889
            };
        } else {
            return new double[]{
                0.5,
                0.4929,
                0.2398,
                0.0142,
                0.0168,
                0.0298,
                0.2645,
                0.3686,
                0.0879,
                0.1128,
                0.0673,
                0.0635,
                0.0048,
                0.0009,
                0.3481
            };
        }
    }

    @Override
    public Thread createMonteCarloThread() {
        return new Thread(() -> {
            try {
                OmahaMonteCarloCalc calcObj = new OmahaMonteCarloCalc(new OmahaHiLoPoker(), cardsPerHand);
                calcObj.calculate(Objects.requireNonNull(boardCardRow.getValue()), playerCardRows.getValue(), new OmahaLiveUpdate(this));
            } catch (InterruptedException ignored) { }
        });
    }

    @Override
    public Thread createExactCalcThread() {
        return new Thread(() -> {
            try {
                OmahaExactCalc calcObj = new OmahaExactCalc(new OmahaHiLoPoker(), cardsPerHand);
                calcObj.calculate(Objects.requireNonNull(boardCardRow.getValue()), playerCardRows.getValue(), new OmahaFinalUpdate(this));
            } catch (InterruptedException ignored) { }
        });
    }
}
