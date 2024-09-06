package com.mogukun.sentry.check;

public class MathUtil {

    public static long gcd(final long current, final long previous) {
        return (previous <= 16384L) ? current : gcd(previous, current % previous);
    }

    public static double gcd(final double a, final double b) {
        try {
            if (a < b) {
                return gcd(b, a);
            }

            if (Math.abs(b) < 0.001) {
                return a;
            } else {
                return gcd(b, a - Math.floor(a / b) * b);
            }
        } catch (StackOverflowError e) {
            return 0;
        }
    }

}
