package com.leslie.cjpokeroddscalculator;

import com.google.common.collect.Sets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

public class GlobalStatic {

    public static Map<Integer, String> rankToStr = new HashMap<>();
    static {
        rankToStr.put(0, "");
        rankToStr.put(2, "2");
        rankToStr.put(3, "3");
        rankToStr.put(4, "4");
        rankToStr.put(5, "5");
        rankToStr.put(6, "6");
        rankToStr.put(7, "7");
        rankToStr.put(8, "8");
        rankToStr.put(9, "9");
        rankToStr.put(10, "T");
        rankToStr.put(11, "J");
        rankToStr.put(12, "Q");
        rankToStr.put(13, "K");
        rankToStr.put(14, "A");
    }

    public static Map<Integer, String> suitToStr = new HashMap<>();
    static {
        suitToStr.put(0, "");
        suitToStr.put(1, "d");
        suitToStr.put(2, "c");
        suitToStr.put(3, "h");
        suitToStr.put(4, "s");
    }

    public static Set<String> suitedSuits = Sets.newHashSet("dd", "cc", "hh", "ss");
    public static Set<String> pairSuits = Sets.newHashSet("hs", "cs", "ds", "ch", "dh", "dc");
    public static Set<String> offSuits = Sets.newHashSet("sh", "sc", "sd", "hs", "hc", "hd", "cs", "ch", "cd", "ds", "dh", "dc");

    static String[] all_possible_cards = new String[] {
            "2d","3d","4d","5d","6d","7d","8d","9d","Td","Jd","Qd","Kd","Ad",
            "2c","3c","4c","5c","6c","7c","8c","9c","Tc","Jc","Qc","Kc","Ac",
            "2h","3h","4h","5h","6h","7h","8h","9h","Th","Jh","Qh","Kh","Ah",
            "2s","3s","4s","5s","6s","7s","8s","9s","Ts","Js","Qs","Ks","As"
    };

    static String[] matrixStrings = {"A", "K", "Q", "J", "T", "9", "8", "7", "6", "5", "4", "3", "2"};

    public static List<List<Set<String>>> copyMatrix(List<List<Set<String>>> original) {
        List<List<Set<String>>> copy = new ArrayList<>(13);
        for (int row_idx = 0; row_idx < 13; row_idx++) {
            List<Set<String>> row = new ArrayList<>(13);
            for (int col_idx = 0; col_idx < 13; col_idx++) {
                row.add(new HashSet<>(original.get(row_idx).get(col_idx)));
            }
            copy.add(row);
        }

        return copy;
    }

    public static boolean isAllSuits(Set<String> suits, int row, int col) {
        if (row == col) {
            return suits.size() == 6;
        } else if (col > row) {
            return suits.size() == 4;
        } else {
            return suits.size() == 12;
        }
    }

    public static void addAllSuits(Set<String> suits, int row, int col) {
        if (row == col) {
            suits.addAll(pairSuits);
        } else if (col > row) {
            suits.addAll(suitedSuits);
        } else {
            suits.addAll(offSuits);
        }
    }

    public static int convertMatrixPositionToRankInt(int matrixPosition) {
        return 14 - matrixPosition;
    }

    public static Map<Integer, Map<Integer, Integer>> suitRankDrawableMap = new HashMap<>();
    static {
        Map<Integer, Integer> temp_map = new HashMap<>();
        temp_map.put(0, R.drawable.unknown_button);
        suitRankDrawableMap.put(0, temp_map);

        temp_map = new HashMap<>();
        temp_map.put(2, R.drawable.d2_button);
        temp_map.put(3, R.drawable.d3_button);
        temp_map.put(4, R.drawable.d4_button);
        temp_map.put(5, R.drawable.d5_button);
        temp_map.put(6, R.drawable.d6_button);
        temp_map.put(7, R.drawable.d7_button);
        temp_map.put(8, R.drawable.d8_button);
        temp_map.put(9, R.drawable.d9_button);
        temp_map.put(10, R.drawable.d10_button);
        temp_map.put(11, R.drawable.d11_button);
        temp_map.put(12, R.drawable.d12_button);
        temp_map.put(13, R.drawable.d13_button);
        temp_map.put(14, R.drawable.d14_button);
        suitRankDrawableMap.put(1, temp_map);

        temp_map = new HashMap<>();
        temp_map.put(2, R.drawable.c2_button);
        temp_map.put(3, R.drawable.c3_button);
        temp_map.put(4, R.drawable.c4_button);
        temp_map.put(5, R.drawable.c5_button);
        temp_map.put(6, R.drawable.c6_button);
        temp_map.put(7, R.drawable.c7_button);
        temp_map.put(8, R.drawable.c8_button);
        temp_map.put(9, R.drawable.c9_button);
        temp_map.put(10, R.drawable.c10_button);
        temp_map.put(11, R.drawable.c11_button);
        temp_map.put(12, R.drawable.c12_button);
        temp_map.put(13, R.drawable.c13_button);
        temp_map.put(14, R.drawable.c14_button);
        suitRankDrawableMap.put(2, temp_map);

        temp_map = new HashMap<>();
        temp_map.put(2, R.drawable.h2_button);
        temp_map.put(3, R.drawable.h3_button);
        temp_map.put(4, R.drawable.h4_button);
        temp_map.put(5, R.drawable.h5_button);
        temp_map.put(6, R.drawable.h6_button);
        temp_map.put(7, R.drawable.h7_button);
        temp_map.put(8, R.drawable.h8_button);
        temp_map.put(9, R.drawable.h9_button);
        temp_map.put(10, R.drawable.h10_button);
        temp_map.put(11, R.drawable.h11_button);
        temp_map.put(12, R.drawable.h12_button);
        temp_map.put(13, R.drawable.h13_button);
        temp_map.put(14, R.drawable.h14_button);
        suitRankDrawableMap.put(3, temp_map);

        temp_map = new HashMap<>();
        temp_map.put(2, R.drawable.s2_button);
        temp_map.put(3, R.drawable.s3_button);
        temp_map.put(4, R.drawable.s4_button);
        temp_map.put(5, R.drawable.s5_button);
        temp_map.put(6, R.drawable.s6_button);
        temp_map.put(7, R.drawable.s7_button);
        temp_map.put(8, R.drawable.s8_button);
        temp_map.put(9, R.drawable.s9_button);
        temp_map.put(10, R.drawable.s10_button);
        temp_map.put(11, R.drawable.s11_button);
        temp_map.put(12, R.drawable.s12_button);
        temp_map.put(13, R.drawable.s13_button);
        temp_map.put(14, R.drawable.s14_button);
        suitRankDrawableMap.put(4, temp_map);
    }

    public static SortedMap<Integer, List<Integer>> bestHandsMap = new TreeMap<>();
    static {
        bestHandsMap.put(6, Arrays.asList(0, 0));
        bestHandsMap.put(12, Arrays.asList(1, 1));
        bestHandsMap.put(18, Arrays.asList(2, 2));
        bestHandsMap.put(24, Arrays.asList(3, 3));
        bestHandsMap.put(30, Arrays.asList(4, 4));
        bestHandsMap.put(34, Arrays.asList(0, 1));
        bestHandsMap.put(40, Arrays.asList(5, 5));
        bestHandsMap.put(44, Arrays.asList(0, 2));
        bestHandsMap.put(56, Arrays.asList(1, 0));
        bestHandsMap.put(60, Arrays.asList(0, 3));
        bestHandsMap.put(64, Arrays.asList(1, 2));
        bestHandsMap.put(70, Arrays.asList(6, 6));
        bestHandsMap.put(74, Arrays.asList(0, 4));
        bestHandsMap.put(86, Arrays.asList(2, 0));
        bestHandsMap.put(90, Arrays.asList(1, 3));
        bestHandsMap.put(94, Arrays.asList(1, 4));
        bestHandsMap.put(98, Arrays.asList(2, 3));
        bestHandsMap.put(110, Arrays.asList(3, 0));
        bestHandsMap.put(122, Arrays.asList(2, 1));
        bestHandsMap.put(126, Arrays.asList(2, 4));
        bestHandsMap.put(130, Arrays.asList(0, 5));
        bestHandsMap.put(136, Arrays.asList(7, 7));
        bestHandsMap.put(148, Arrays.asList(4, 0));
        bestHandsMap.put(152, Arrays.asList(3, 4));
        bestHandsMap.put(164, Arrays.asList(3, 1));
        bestHandsMap.put(168, Arrays.asList(0, 6));
        bestHandsMap.put(172, Arrays.asList(1, 5));
        bestHandsMap.put(184, Arrays.asList(3, 2));
        bestHandsMap.put(188, Arrays.asList(0, 7));
        bestHandsMap.put(200, Arrays.asList(4, 1));
        bestHandsMap.put(204, Arrays.asList(2, 5));
        bestHandsMap.put(208, Arrays.asList(0, 9));
        bestHandsMap.put(214, Arrays.asList(8, 8));
        bestHandsMap.put(218, Arrays.asList(0, 8));
        bestHandsMap.put(230, Arrays.asList(4, 2));
        bestHandsMap.put(234, Arrays.asList(3, 5));
        bestHandsMap.put(246, Arrays.asList(5, 0));
        bestHandsMap.put(250, Arrays.asList(4, 5));
        bestHandsMap.put(254, Arrays.asList(0, 10));
        bestHandsMap.put(258, Arrays.asList(1, 6));
        bestHandsMap.put(270, Arrays.asList(4, 3));
        bestHandsMap.put(274, Arrays.asList(1, 7));
        bestHandsMap.put(286, Arrays.asList(6, 0));
        bestHandsMap.put(290, Arrays.asList(0, 11));
        bestHandsMap.put(294, Arrays.asList(2, 6));
        bestHandsMap.put(306, Arrays.asList(5, 1));
        bestHandsMap.put(310, Arrays.asList(0, 12));
        bestHandsMap.put(314, Arrays.asList(1, 8));
        bestHandsMap.put(318, Arrays.asList(3, 6));
        bestHandsMap.put(322, Arrays.asList(4, 6));
        bestHandsMap.put(334, Arrays.asList(7, 0));
        bestHandsMap.put(340, Arrays.asList(9, 9));
        bestHandsMap.put(352, Arrays.asList(5, 2));
        bestHandsMap.put(356, Arrays.asList(5, 6));
        bestHandsMap.put(360, Arrays.asList(1, 9));
        bestHandsMap.put(364, Arrays.asList(2, 7));
        bestHandsMap.put(376, Arrays.asList(5, 3));
        bestHandsMap.put(388, Arrays.asList(9, 0));
        bestHandsMap.put(400, Arrays.asList(5, 4));
        bestHandsMap.put(412, Arrays.asList(8, 0));
        bestHandsMap.put(416, Arrays.asList(1, 10));
        bestHandsMap.put(428, Arrays.asList(6, 1));
        bestHandsMap.put(432, Arrays.asList(2, 8));
        bestHandsMap.put(436, Arrays.asList(3, 7));
        bestHandsMap.put(440, Arrays.asList(4, 7));
        bestHandsMap.put(452, Arrays.asList(10, 0));
        bestHandsMap.put(456, Arrays.asList(5, 7));
        bestHandsMap.put(460, Arrays.asList(1, 11));
        bestHandsMap.put(464, Arrays.asList(6, 7));
        bestHandsMap.put(468, Arrays.asList(2, 9));
        bestHandsMap.put(480, Arrays.asList(7, 1));
        bestHandsMap.put(486, Arrays.asList(10, 10));
        bestHandsMap.put(498, Arrays.asList(6, 2));
        bestHandsMap.put(510, Arrays.asList(11, 0));
        bestHandsMap.put(514, Arrays.asList(1, 12));
        bestHandsMap.put(526, Arrays.asList(6, 3));
        bestHandsMap.put(530, Arrays.asList(2, 10));
        bestHandsMap.put(542, Arrays.asList(6, 4));
        bestHandsMap.put(546, Arrays.asList(3, 8));
        bestHandsMap.put(558, Arrays.asList(8, 1));
        bestHandsMap.put(570, Arrays.asList(12, 0));
        bestHandsMap.put(574, Arrays.asList(4, 8));
        bestHandsMap.put(586, Arrays.asList(6, 5));
        bestHandsMap.put(590, Arrays.asList(7, 8));
        bestHandsMap.put(594, Arrays.asList(6, 8));
        bestHandsMap.put(598, Arrays.asList(5, 8));
        bestHandsMap.put(602, Arrays.asList(2, 11));
        bestHandsMap.put(606, Arrays.asList(3, 9));
        bestHandsMap.put(618, Arrays.asList(9, 1));
        bestHandsMap.put(630, Arrays.asList(7, 2));
        bestHandsMap.put(634, Arrays.asList(2, 12));
        bestHandsMap.put(638, Arrays.asList(3, 10));
        bestHandsMap.put(644, Arrays.asList(11, 11));
        bestHandsMap.put(648, Arrays.asList(8, 9));
        bestHandsMap.put(660, Arrays.asList(7, 3));
        bestHandsMap.put(672, Arrays.asList(7, 4));
        bestHandsMap.put(684, Arrays.asList(10, 1));
        bestHandsMap.put(688, Arrays.asList(7, 9));
        bestHandsMap.put(692, Arrays.asList(4, 9));
        bestHandsMap.put(704, Arrays.asList(8, 2));
        bestHandsMap.put(708, Arrays.asList(3, 11));
        bestHandsMap.put(712, Arrays.asList(5, 9));
        bestHandsMap.put(724, Arrays.asList(7, 6));
        bestHandsMap.put(728, Arrays.asList(6, 9));
        bestHandsMap.put(740, Arrays.asList(7, 5));
        bestHandsMap.put(744, Arrays.asList(4, 10));
        bestHandsMap.put(756, Arrays.asList(11, 1));
        bestHandsMap.put(760, Arrays.asList(3, 12));
        bestHandsMap.put(764, Arrays.asList(9, 10));
        bestHandsMap.put(776, Arrays.asList(9, 2));
        bestHandsMap.put(780, Arrays.asList(8, 10));
        bestHandsMap.put(784, Arrays.asList(4, 11));
        bestHandsMap.put(790, Arrays.asList(12, 12));
        bestHandsMap.put(802, Arrays.asList(12, 1));
        bestHandsMap.put(806, Arrays.asList(7, 10));
        bestHandsMap.put(818, Arrays.asList(8, 7));
        bestHandsMap.put(822, Arrays.asList(4, 12));
        bestHandsMap.put(834, Arrays.asList(10, 2));
        bestHandsMap.put(846, Arrays.asList(8, 3));
        bestHandsMap.put(850, Arrays.asList(6, 10));
        bestHandsMap.put(854, Arrays.asList(5, 10));
        bestHandsMap.put(866, Arrays.asList(8, 6));
        bestHandsMap.put(878, Arrays.asList(8, 4));
        bestHandsMap.put(890, Arrays.asList(8, 5));
        bestHandsMap.put(894, Arrays.asList(9, 11));
        bestHandsMap.put(898, Arrays.asList(5, 11));
        bestHandsMap.put(910, Arrays.asList(11, 2));
        bestHandsMap.put(922, Arrays.asList(9, 3));
        bestHandsMap.put(926, Arrays.asList(8, 11));
        bestHandsMap.put(930, Arrays.asList(10, 11));
        bestHandsMap.put(934, Arrays.asList(5, 12));
        bestHandsMap.put(938, Arrays.asList(7, 11));
        bestHandsMap.put(950, Arrays.asList(9, 8));
        bestHandsMap.put(962, Arrays.asList(12, 2));
        bestHandsMap.put(974, Arrays.asList(10, 3));
        bestHandsMap.put(978, Arrays.asList(6, 11));
        bestHandsMap.put(990, Arrays.asList(9, 7));
        bestHandsMap.put(994, Arrays.asList(9, 12));
        bestHandsMap.put(1006, Arrays.asList(9, 6));
        bestHandsMap.put(1010, Arrays.asList(6, 12));
        bestHandsMap.put(1022, Arrays.asList(9, 4));
        bestHandsMap.put(1034, Arrays.asList(9, 5));
        bestHandsMap.put(1046, Arrays.asList(11, 3));
        bestHandsMap.put(1050, Arrays.asList(8, 12));
        bestHandsMap.put(1062, Arrays.asList(10, 9));
        bestHandsMap.put(1066, Arrays.asList(10, 12));
        bestHandsMap.put(1078, Arrays.asList(10, 4));
        bestHandsMap.put(1090, Arrays.asList(12, 3));
        bestHandsMap.put(1094, Arrays.asList(7, 12));
        bestHandsMap.put(1106, Arrays.asList(10, 8));
        bestHandsMap.put(1118, Arrays.asList(11, 4));
        bestHandsMap.put(1122, Arrays.asList(11, 12));
        bestHandsMap.put(1134, Arrays.asList(10, 7));
        bestHandsMap.put(1146, Arrays.asList(10, 6));
        bestHandsMap.put(1158, Arrays.asList(12, 4));
        bestHandsMap.put(1170, Arrays.asList(10, 5));
        bestHandsMap.put(1182, Arrays.asList(11, 9));
        bestHandsMap.put(1194, Arrays.asList(11, 5));
        bestHandsMap.put(1206, Arrays.asList(11, 8));
        bestHandsMap.put(1218, Arrays.asList(11, 10));
        bestHandsMap.put(1230, Arrays.asList(12, 5));
        bestHandsMap.put(1242, Arrays.asList(11, 7));
        bestHandsMap.put(1254, Arrays.asList(11, 6));
        bestHandsMap.put(1266, Arrays.asList(12, 9));
        bestHandsMap.put(1278, Arrays.asList(12, 6));
        bestHandsMap.put(1290, Arrays.asList(12, 10));
        bestHandsMap.put(1302, Arrays.asList(12, 8));
        bestHandsMap.put(1314, Arrays.asList(12, 7));
        bestHandsMap.put(1326, Arrays.asList(12, 11));
    }
}