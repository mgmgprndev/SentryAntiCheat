package com.mogukun.sentry.models;

import java.util.concurrent.ConcurrentLinkedDeque;

public class BetterCounter {

    ConcurrentLinkedDeque<DoubleLong> counts = new ConcurrentLinkedDeque<>();

    public BetterCounter() {}

    public double count(double d) {
        long now = System.currentTimeMillis();
        counts.add( new DoubleLong(d,now) );
        return getCount(now);
    }

    public double getCount() {
        return getCount(System.currentTimeMillis());
    }

    private double getCount(long now) {
        counts.removeIf(k -> now - k.b > 1000);
        double t = 0;
        for ( DoubleLong l : counts ) t += l.a;
        return t;
    }

    public void clear() {
        counts.clear();
    }

}
