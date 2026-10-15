package com.chinatower.gis.utils;

import java.math.BigDecimal;

public class LayerTaoheUtil {


    public static double roundDouble2Pot(double val) {
        return roundDoubleNPot(2, val);
    }
    public static double roundDouble4Pot(double val) {
        return roundDoubleNPot(4, val);
    }
    public static double roundDoubleNPot(int n, double val) {
        BigDecimal b = new BigDecimal(val);
        double f1 = b.setScale(n, BigDecimal.ROUND_HALF_UP).doubleValue();
        return f1;
    }

    public static String saveDouble4Pot(double val) {
        return saveDoubleNPot(4, val);
    }
    public static String saveDouble2Pot(double val) {
        return saveDoubleNPot(2, val);
    }
    public static String saveDoubleNPot(int n, double val) {
        String s = String.format("%." + n + "f", val);
        return  s;
    }

    public static String getNewTmpTbName(String type) {
        String tbName = "TMP_" + type + "_" + System.currentTimeMillis();
        return tbName;
    }

    public static String getZerpString() {
        return "0";
    }
}
