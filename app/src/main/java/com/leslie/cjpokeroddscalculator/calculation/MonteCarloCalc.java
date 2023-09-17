package com.leslie.cjpokeroddscalculator.calculation;

import com.leslie.cjpokeroddscalculator.OutputResult;

import java.util.Random;

public class MonteCarloCalc extends Calculation{
    static Random myRandom = new Random();
    public void monte_carl_calc(int[][][] all_cards, int players_remaining_no, OutputResult output_result_obj) throws InterruptedException {
        pre_simulation_calc(all_cards, players_remaining_no);

        total_simulations = this.max_simulation;

        output_result_obj.before_all_simulation();

        for(this.simulation_count = 1; simulation_count <= total_simulations; simulation_count++){
            int[] random_numbers = random_no_generator(no_of_unknown_cards, no_of_known_cards, deck);

            scenario_calc(random_numbers);

            output_result_obj.after_every_simulation();
        }

        output_result_obj.after_all_simulation();
    }

    public static int[] random_no_generator(int no_of_unknown_cards, int no_of_known_cards, int[] deck) {

        int i, temp;
        int[] random_numbers = new int[no_of_unknown_cards];
        int[] deck2 = new int[52];

        for(i = 0; i < 52; i++) {
            deck2[i] = deck[i];
        }

        for(i = 0; i < no_of_unknown_cards; i++){
            temp = myRandom.nextInt(52 - no_of_known_cards - i);
            random_numbers[i] = deck2[temp];

            deck2[temp] = deck2[51 - no_of_known_cards - i];
        }

        return random_numbers;
    }

}
