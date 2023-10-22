package com.leslie.cjpokeroddscalculator;

import java.util.HashMap;

public class GlobalStatic {

    public static HashMap<Integer, String> rankToStr = new HashMap<>();
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

    public static HashMap<Integer, String> suitToStr = new HashMap<>();
    static {
        suitToStr.put(0, "");
        suitToStr.put(1, "d");
        suitToStr.put(2, "c");
        suitToStr.put(3, "h");
        suitToStr.put(4, "s");
    }

    static String[] all_possible_cards = new String[] {
            "2d","3d","4d","5d","6d","7d","8d","9d","Td","Jd","Qd","Kd","Ad",
            "2c","3c","4c","5c","6c","7c","8c","9c","Tc","Jc","Qc","Kc","Ac",
            "2h","3h","4h","5h","6h","7h","8h","9h","Th","Jh","Qh","Kh","Ah",
            "2s","3s","4s","5s","6s","7s","8s","9s","Ts","Js","Qs","Ks","As"
    };


    static String[] matrixStrings = {"A", "K", "Q", "J", "T", "9", "8", "7", "6", "5", "4", "3", "2"};

}
