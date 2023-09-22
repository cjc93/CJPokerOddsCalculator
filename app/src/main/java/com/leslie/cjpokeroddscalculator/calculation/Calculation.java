package com.leslie.cjpokeroddscalculator.calculation;

import java.util.Arrays;
import java.util.stream.IntStream;

public class Calculation {
    private int players_remaining_no;
    private int[] known_players;
    private double[] equity_total;
    public int simulation_count;
    private int no_of_unknown_players;
    public int no_of_unknown_cards;
    public int no_of_known_cards;
    public int[] deck;
    private int[][][] all_cards_copy;
    private int[][] unknown_positions;
    public final int max_simulation = 2000000000;
    public int total_simulations;

    public void pre_simulation_calc(int[][][] all_cards, int players_remaining_no) {
        this.players_remaining_no = players_remaining_no;

        this.unknown_positions = new int[players_remaining_no * 2 + 5][2];
        this.no_of_unknown_cards = 0;
        this.no_of_known_cards = 0;
        this.deck = IntStream.rangeClosed(0, 51).toArray();

        for (int row = 0; row <= players_remaining_no; row++) {
            for (int card = 0; card < all_cards[row].length; card++) {
                if(all_cards[row][card][0] == 0) {
                    unknown_positions[no_of_unknown_cards][0] = row;
                    unknown_positions[no_of_unknown_cards][1] = card;
                    no_of_unknown_cards++;
                } else {
                    deck[(all_cards[row][card][0] - 1) * 13 + all_cards[row][card][1] - 2] = 52;
                    no_of_known_cards++;
                }
            }
        }

        Calculation.insertion_srt_array(deck, 52);

        this.known_players = new int[players_remaining_no];
        this.no_of_unknown_players = 0;

        for(int player = 1; player <= players_remaining_no; player++) {
            if(all_cards[player][0][0] != 0 || all_cards[player][1][0] != 0) {
                known_players[player - 1] = 1;
            } else {
                no_of_unknown_players++;
            }
        }

        this.all_cards_copy = new int[players_remaining_no + 1][][];
        for (int i = 0; i <= players_remaining_no; i++) {
            all_cards_copy[i] = new int[all_cards[i].length][];
            for (int j = 0; j < all_cards[i].length; j++) {
                all_cards_copy[i][j] = Arrays.copyOf(all_cards[i][j], all_cards[i][j].length);
            }
        }

        this.equity_total = new double[players_remaining_no];
    }

    public void scenario_calc(int[] scenario_numbers) {
        for(int j = 0; j < no_of_unknown_cards; j++){
            this.all_cards_copy[this.unknown_positions[j][0]][unknown_positions[j][1]][0] = scenario_numbers[j] / 13 + 1;
            all_cards_copy[unknown_positions[j][0]][unknown_positions[j][1]][1] = scenario_numbers[j] % 13 + 2;
        }

        int[] game_stats = Calculation.game_judge(all_cards_copy, players_remaining_no, known_players);

        int split_total = 0;
        for(int i : game_stats) {
            split_total += i;
        }

        for(int j = 0; j < players_remaining_no; j++) {
            if(known_players[j] == 1) {
                equity_total[j] += (double) game_stats[j] / (double) split_total;
            }
        }
    }

    public double[] calc_equity_perc() {
        double[] equity_perc = new double[this.players_remaining_no];
        double known_players_equity = 0;

        for(int i = 0; i < players_remaining_no; i++) {
            if(this.known_players[i] == 1) {
                equity_perc[i] = this.equity_total[i] / this.simulation_count;
                known_players_equity += equity_perc[i];
            }
        }

        double unknown_players_equity = (1 - known_players_equity) / this.no_of_unknown_players;
        for(int i = 0; i < players_remaining_no; i++) {
            if(known_players[i] == 0) {
                equity_perc[i] = unknown_players_equity;
            }
        }

        return equity_perc;
    }


    public static void insertion_srt_array(int[] deck, int n) {
        for (int i = 1; i < n; i++) {
            int j = i;
            int B = deck[i];
            while ((j > 0) && (deck[j-1] > B)) {
                deck[j] = deck[j-1];
                j--;
            }
            deck[j] = B;
        }
    }


    public static int[] game_judge(int[][][] all_cards, int players_remaining_no, int[] known_players) {
        int[] game_stats = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        int j;
        int[] best_hand = {0, 0, 0, 0, 0, 0};

        int[][] pre_sorted_cards = new int[][] {
            {all_cards[0][0][0], all_cards[0][0][1]},
            {all_cards[0][1][0], all_cards[0][1][1]},
            {all_cards[0][2][0], all_cards[0][2][1]},
            {all_cards[0][3][0], all_cards[0][3][1]},
            {all_cards[0][4][0], all_cards[0][4][1]}
        };

        insertion_srt(pre_sorted_cards, 5);

        for(int i = 1; i <= players_remaining_no; i++) {
            if(known_players[i - 1] == 1) {
                int[] decision = decide_hand_for_player(pre_sorted_cards, all_cards, i, best_hand);

                for(j = 0; j < 6; j++) {
                    if(decision[j] > best_hand[j]) {
                        best_hand = decision;
                        for(int k = 0; k < 10; k++) {
                            game_stats[k] = 0;
                        }
                        game_stats[i - 1] = 1;
                        break;
                    } else if(decision[j] < best_hand[j]) {
                        break;
                    }
                }

                if(j == 6) {
                    game_stats[i - 1] = 1;
                }
            }
        }

        boolean unknown_player_win = false;
        for(int i = 1; i <= players_remaining_no; i++) {
            if(known_players[i - 1] == 0) {
                int[] decision = decide_hand_for_player(pre_sorted_cards, all_cards, i, best_hand);

                for(j = 0; j < 6; j++) {
                    if(decision[j] > best_hand[j]) {
                        for(int k = 0; k < 10; k++) {
                            game_stats[k] = 0;
                        }
                        game_stats[i - 1] = 1;
                        unknown_player_win = true;
                        break;
                    } else if(decision[j] < best_hand[j]) {
                        break;
                    }
                }

                if(unknown_player_win) {
                    break;
                }

                if(j == 6) {
                    game_stats[i - 1] = 1;
                }
            }
        }

        return game_stats;
    }

    private static int[] decide_hand_for_player(int[][] preSortedCards, int[][][] allCards, int player, int[] bestHand) {
        int[][] sorted_cards = new int[][] {
            {preSortedCards[0][0], preSortedCards[0][1]},
            {preSortedCards[1][0], preSortedCards[1][1]},
            {preSortedCards[2][0], preSortedCards[2][1]},
            {preSortedCards[3][0], preSortedCards[3][1]},
            {preSortedCards[4][0], preSortedCards[4][1]},
            {allCards[player][0][0], allCards[player][0][1]},
            {allCards[player][1][0], allCards[player][1][1]}
        };

        insertion_srt(sorted_cards, 7);

        return decide_hand(bestHand, sorted_cards);
    }

    private static void insertion_srt(int[][] pre_sorted_cards, int n) {

        for (int i = 1; i < n; i++) {

            int j = i;
            int B0 = pre_sorted_cards[i][0];
            int B1 = pre_sorted_cards[i][1];

            while (j > 0 && pre_sorted_cards[j - 1][1] < B1) {
                pre_sorted_cards[j][0] = pre_sorted_cards[j - 1][0];
                pre_sorted_cards[j][1] = pre_sorted_cards[j - 1][1];
                j--;
            }

            pre_sorted_cards[j][0] = B0;
            pre_sorted_cards[j][1] = B1;
        }
    }


    private static int[] decide_hand(int[] best_hand, int[][] sorted_cards) {
        int[] decision = straight_flush(sorted_cards);

        if(decision[0] > 0 || best_hand[0] == 9) {
            return decision;
        }

        decision = four_of_a_kind(sorted_cards);

        if(decision[0] > 0 || best_hand[0] >= 8) {
            return decision;
        }

        decision = full_house(sorted_cards);

        if(decision[0] > 0 || best_hand[0] >= 7) {
            return decision;
        }

        decision = flush(sorted_cards);

        if(decision[0] > 0 || best_hand[0] >= 6) {
            return decision;
        }

        decision = straight(sorted_cards);

        if(decision[0] > 0 || best_hand[0] >= 5) {
            return decision;
        }

        decision = three_of_a_kind(sorted_cards);

        if(decision[0] > 0 || best_hand[0] >= 4) {
            return decision;
        }

        decision = pairs(sorted_cards);

        if(decision[0] > 0 || best_hand[0] >= 2) {
            return decision;
        }

        decision = high_card(sorted_cards);

        return decision;
    }


    private static int[] high_card(int[][] sorted_cards) {
        return new int[] {
            1,
            sorted_cards[0][1],
            sorted_cards[1][1],
            sorted_cards[2][1],
            sorted_cards[3][1],
            sorted_cards[4][1]
        };
    }


    private static int[] pairs(int[][] sorted_cards) {
        int[] decision = {0, 0, 0, 0, 0, 0};
        int kicker = 0;

        for(int i = 0; i <= 5; i++) {
            if(sorted_cards[i][1] == sorted_cards[i + 1][1]) {
                for(int j = i + 2; j <= 5; j++) {
                    if(sorted_cards[j][1] == sorted_cards[j + 1][1]) {
                        for(int k = 0; k <= 4; k++) {
                            if(sorted_cards[k][1] != sorted_cards[i][1] && sorted_cards[k][1] != sorted_cards[j][1]) {
                                decision[0] = 3;
                                decision[1] = sorted_cards[i][1];
                                decision[2] = sorted_cards[j][1];
                                decision[3] = sorted_cards[k][1];
                                return decision;
                            }
                        }
                    }
                }

                decision[0] = 2;
                decision[1] = sorted_cards[i][1];

                for(int j = 0; j <= 4; j++) {
                    if(sorted_cards[j][1] != sorted_cards[i][1]) {
                        kicker++;
                        decision[kicker + 1] = sorted_cards[j][1];
                        if(kicker == 3) {
                            return decision;
                        }
                    }
                }
            }
        }

        return decision;
    }


    private static int[] three_of_a_kind(int[][] sorted_cards) {
        int[] decision = {0, 0, 0, 0, 0, 0};
        int j, kicker = 0;

        for(int i = 0; i <= 4; i++) {
            for(j = i + 1; j <= i + 2; j++) {
                if(sorted_cards[i][1] != sorted_cards[j][1]) {
                    break;
                }
            }

            if(j == i + 3) {
                decision[0] = 4;
                decision[1] = sorted_cards[i][1];

                for(j = 0; j <= 6; j++) {
                    if(sorted_cards[j][1] != sorted_cards[i][1]) {
                        kicker++;
                        decision[kicker + 1] = sorted_cards[j][1];
                        if(kicker == 2) {
                            return decision;
                        }
                    }
                }
            }
        }

        return decision;
    }


    private static int[] straight(int[][] sorted_cards) {
        int[] decision = {0, 0, 0, 0, 0, 0};
        int j, k;

        for(int i = 0; i <= 2; i++) {
            k = i + 1;
            j1loop:
            for(j = 1; j <= 4; j++ ) {
                for( ; k <= 6; k++) {
                    if(sorted_cards[i][1] - j == sorted_cards[k][1]) {
                        break;
                    } else if (sorted_cards[i][1] - j > sorted_cards[k][1]) {
                        break j1loop;
                    }
                }
                if(k == 7) {
                    break;
                }
            }
            if(j == 5) {
                decision[0] = 5;
                decision[1] = sorted_cards[i][1];
                return decision;
            }
        }

        if(sorted_cards[0][1] == 14) {
            k = 1;
            j2loop:
            for(j = 5; j >= 2; j--) {
                for( ; k <= 6; k++) {
                    if(sorted_cards[k][1] == j) {
                        break;
                    } else if (j > sorted_cards[k][1]) {
                        break j2loop;
                    }
                }
                if(k == 7) {
                    break;
                }
            }
            if(j == 1) {
                decision[0] = 5;
                decision[1] = 5;
                return decision;
            }
        }
        return decision;
    }


    private static int[] flush(int[][] sorted_cards) {
        int[] decision = {0, 0, 0, 0, 0, 0};

        for(int i = 0; i <= 2; i++) {
            decision[1] = sorted_cards[i][1];
            int count = 1;
            for(int j = i + 1; j <= 6; j++) {
                if(sorted_cards[i][0] == sorted_cards[j][0]) {
                    count++;
                    decision[count] = sorted_cards[j][1];
                    if(count == 5) {
                        decision[0] = 6;
                        return decision;
                    }
                }
            }
        }

        return decision;
    }


    private static int[] full_house(int[][] sorted_cards) {
        int[] decision = {0, 0, 0, 0, 0, 0};
        int i, j;

        for(i = 0; i <= 4; i++) {
            for(j = i + 1; j <= i + 2; j++) {
                if(sorted_cards[i][1] != sorted_cards[j][1]) {
                    break;
                }
            }

            if(j == i + 3) {
                for(j = 0; j <= 5; j++) {
                    if(sorted_cards[j][1] == sorted_cards[j + 1][1] && sorted_cards[j][1] != sorted_cards[i][1]) {
                        decision[0] = 7;
                        decision[1] = sorted_cards[i][1];
                        decision[2] = sorted_cards[j][1];
                        return decision;
                    }
                }
                return decision;
            }

        }

        return decision;
    }


    private static int[] four_of_a_kind(int[][] sorted_cards) {
        int[] decision = {0, 0, 0, 0, 0, 0};
        int i, j;

        for(i = 0; i <= 3; i++) {
            for(j = i + 1; j <= i + 3; j++) {
                if(sorted_cards[i][1] != sorted_cards[j][1]) {
                    break;
                }
            }

            if(j == i + 4) {
                decision[0] = 8;
                decision[1] = sorted_cards[i][1];

                for(j = 0; j <= 4; j++) {
                    if(sorted_cards[j][1] != sorted_cards[i][1]) {
                        decision[2] = sorted_cards[j][1];
                        return decision;
                    }
                }
            }
        }

        return decision;
    }


    private static int[] straight_flush(int[][] sorted_cards) {
        int[] decision = {0, 0, 0, 0, 0, 0};
        int j, k;

        for(int i = 0; i <= 2; i++) {
            k = i + 1;
            j1loop:
            for(j = 1; j <= 4; j++ ) {
                for( ; k <= 6; k++) {
                    if(sorted_cards[i][0] == sorted_cards[k][0] && sorted_cards[i][1] - j == sorted_cards[k][1]) {
                        break;
                    } else if (sorted_cards[i][1] - j > sorted_cards[k][1]) {
                        break j1loop;
                    }
                }
                if(k == 7) {
                    break;
                }
            }
            if(j == 5) {
                decision[0] = 9;
                decision[1] = sorted_cards[i][1];
                return decision;
            }
        }

        for(int i = 0; i <= 2; i++) {
            if(sorted_cards[i][1] == 14) {
                k = i + 1;
                j2loop:
                for(j = 5; j >= 2; j--) {
                    for( ; k <= 6; k++) {
                        if(sorted_cards[k][0] == sorted_cards[i][0] && sorted_cards[k][1] == j) {
                            break;
                        } else if (j > sorted_cards[k][1]) {
                            break j2loop;
                        }
                    }
                    if(k == 7) {
                        break;
                    }
                }
                if(j == 1) {
                    decision[0] = 9;
                    decision[1] = 5;
                    return decision;
                }
            }
        }
        return decision;
    }
}
