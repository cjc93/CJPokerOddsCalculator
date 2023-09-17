package com.leslie.cjpokeroddscalculator.calculation;

import com.leslie.cjpokeroddscalculator.OutputResult;

public class ExactCalc extends Calculation {
    public OutputResult outputResultObj;

    public void exact_calc(int[][][] all_cards, int players_remaining_no, OutputResult output_result_obj) throws InterruptedException {
        this.outputResultObj = output_result_obj;
        pre_simulation_calc(all_cards, players_remaining_no);

        simulation_count = 0;
        this.total_simulations = calc_total_simulations(52 - no_of_known_cards, no_of_unknown_cards);

        if (this.total_simulations == -1) {
            throw new InterruptedException();
        }
        else {
            int[] remaining_cards_in_deck = new int[52 - no_of_known_cards];
            if (52 - no_of_known_cards >= 0)
                System.arraycopy(deck, 0, remaining_cards_in_deck, 0, 52 - no_of_known_cards);

            this.outputResultObj.before_all_simulation();

            choose(remaining_cards_in_deck, no_of_unknown_cards);

            outputResultObj.after_all_simulation();
        }
    }

    public int calc_total_simulations(int n, int r) {
        // nPr calculation
        int res = 1;
        for (int i = n; i > n - r; i--) {
            if (res < max_simulation / i) {
                res *= i;
            }
            else {
                return -1;
            }
        }

        return res;
    }
    public void choose(int[] a, int k) throws InterruptedException {
        enumerate(a, a.length, k);
    }

    private void enumerate(int[] a, int n, int k) throws InterruptedException {
        if (k == 0) {
            int[] singlePermutation = new int[a.length - n];
            if (a.length - n >= 0) System.arraycopy(a, n, singlePermutation, 0, a.length - n);

            scenario_calc(singlePermutation);

            this.outputResultObj.after_every_simulation();

            simulation_count++;

            return;
        }

        for (int i = 0; i < n; i++) {
            swap(a, i, n-1);
            enumerate(a, n-1, k-1);
            swap(a, i, n-1);
        }
    }

    public static void swap(int[] a, int i, int j) {
        int temp = a[i];
        a[i] = a[j];
        a[j] = temp;
    }
}
