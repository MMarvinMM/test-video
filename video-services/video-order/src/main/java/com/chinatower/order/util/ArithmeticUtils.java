package com.chinatower.order.util;

public class ArithmeticUtils {

    /**
     * 递归实现伽马函数
     *
     * @param x
     * @param setAbsRelaErr 相对误差绝对值
     * @return
     */
    public static double gamma(double x, double setAbsRelaErr) {
        //setAbsRelaErr 相对误差绝对值
        //递归结束条件
        if (x < 0) {
            return gamma(x + 1, setAbsRelaErr) / x;
        }
        if (Math.abs(1.0 - x) < 0.00001) {
            return 1;
        }
        if (Math.abs(0.5 - x) < 0.00001) {
            return Math.sqrt(3.1415926);
        }

        if (x > 1.0) {
            return (x - 1) * gamma(x - 1, setAbsRelaErr);
        }

        double res = 0.0;
        double temp = 1.0;
        double check = 0.0;
        int i = 1;
        while (Math.abs((check - temp) / temp) > setAbsRelaErr) {
            check = temp;
            temp *= i / (x - 1 + i);
            i++;
        }
        res = temp * Math.pow(i, x - 1);
        return res;
    }

    /**
     * 概率密度函数
     *
     * @param x     某时段降水量 x
     * @param b     尺度
     * @param r     形状参数
     * @return 降水概率密度
     */
    public static double proDensity(double x, double b, double r) {
        double e = Math.E;//自然常数e的近似值
        double f = gamma(r, 0.00001);
        return Math.pow(x, r - 1) * Math.exp(-x / b) / (Math.pow(b, r) * f);
    }

    /**
     * spi函数
     *
     * @param F 概率
     * @return 标准化降水指数
     */
    public static double spi(double F) {
        double S;
        double C0 = 2.515517;
        double C1 = 0.802853;
        double C2 = 0.010328;
        double D1 = 1.432788;
        double D2 = 0.189269;
        double D3 = 0.001308;
        if (F > 0.5) {
            F = 1.0 - F;
            S = 1;
        } else {
            S = -1;
        }
        double T = Math.sqrt(Math.log(1 / Math.pow(F, 2)));
        double Z = (T - ((C2 * T + C1) * T + C0) / (((D3 * T + D2) * T + D1) * T + 1.0)) * S;

        return Z;
    }
}
