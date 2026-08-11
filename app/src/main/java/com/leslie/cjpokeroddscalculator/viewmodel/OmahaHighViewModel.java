package com.leslie.cjpokeroddscalculator.viewmodel;

import androidx.lifecycle.SavedStateHandle;

import com.leslie.cjpokeroddscalculator.calculation.OmahaExactCalc;
import com.leslie.cjpokeroddscalculator.calculation.OmahaMonteCarloCalc;
import com.leslie.cjpokeroddscalculator.calculation.pet.OmahaPoker;
import com.leslie.cjpokeroddscalculator.outputresult.OmahaFinalUpdate;
import com.leslie.cjpokeroddscalculator.outputresult.OmahaLiveUpdate;

import java.util.Objects;

public class OmahaHighViewModel extends EquityCalculatorViewModel {
    public OmahaHighViewModel(SavedStateHandle savedStateHandle) {
        super(savedStateHandle);
    }

    @Override
    public double[] getInitialStats() {
        if (cardsPerHand == 5) {
            return new double[]{
                0.5,
                0.4898,
                0.0204,
                0.0078,
                0.1668,
                0.376,
                0.0966,
                0.1525,
                0.0961,
                0.0956,
                0.0072,
                0.0014
            };
        } else if (cardsPerHand == 6) {
            return new double[]{
                0.5,
                0.4864,
                0.0273,
                0.0014,
                0.0978,
                0.3479,
                0.1032,
                0.1860,
                0.1224,
                0.1289,
                0.0101,
                0.0022
            };
        } else {
            return new double[]{
                0.5,
                0.4929,
                0.0142,
                0.0299,
                0.2647,
                0.3683,
                0.0879,
                0.1127,
                0.0672,
                0.0635,
                0.0048,
                0.0009
            };
        }
    }

    @Override
    public Thread createMonteCarloThread() {
        return new Thread(() -> {
            try {
                OmahaMonteCarloCalc calcObj = new OmahaMonteCarloCalc(new OmahaPoker(), cardsPerHand);
                calcObj.calculate(Objects.requireNonNull(boardCardRow.getValue()), playerCardRows.getValue(), new OmahaLiveUpdate(this));
            } catch (InterruptedException ignored) { }
        });
    }

    @Override
    public Thread createExactCalcThread() {
        return new Thread(() -> {
            try {
                OmahaExactCalc calcObj = new OmahaExactCalc(new OmahaPoker(), cardsPerHand);
                calcObj.calculate(Objects.requireNonNull(boardCardRow.getValue()), playerCardRows.getValue(), new OmahaFinalUpdate(this));
            } catch (InterruptedException ignored) { }
        });
    }
}
