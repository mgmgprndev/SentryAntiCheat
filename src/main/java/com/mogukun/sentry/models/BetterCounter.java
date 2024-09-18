package com.mogukun.sentry.models;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedDeque;

public class BetterCounter {

    ConcurrentLinkedDeque<DoubleLong> counts = new ConcurrentLinkedDeque<>();

    long interval = 1000000000;

    public BetterCounter() {}

    public BetterCounter(long interval) {
        this.interval = interval * 1000000;
    }

    public double count(double d) {
        long now = System.nanoTime();
        counts.add( new DoubleLong(d,now) );
        return getCount(now);
    }

    public double getCount() {
        return getCount(System.nanoTime());
    }

    private double getCount(long now) {
        counts.removeIf(k -> now - k.b >= interval);
        double t = 0;
        for ( DoubleLong l : counts ) t += l.a;
        return t;
    }

    public void clear() {
        counts.clear();
    }

    public int size() {
        return counts.size();
    }

    public ArrayList<Double> getDoubleList() {
        ArrayList<Double> temp = new ArrayList<>();
        for ( DoubleLong l : counts )  temp.add(l.a);
        return temp;
    }

    public ArrayList<Long> getLongList() {
        ArrayList<Long> temp = new ArrayList<>();
        for ( DoubleLong l : counts )  temp.add((long)l.a);
        return temp;
    }

}
