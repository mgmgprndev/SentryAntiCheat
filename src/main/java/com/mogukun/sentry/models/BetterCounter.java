package com.mogukun.sentry.models;

import java.util.ArrayList;
import java.util.Collections;
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
        counts.removeIf(k -> now - k.b > interval);
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

    public double averageDouble() {
        ArrayList<Double> list = getDoubleList();
        Collections.sort(list);
        int size = list.size();
        double q1 = list.get(size / 4);
        double q3 = list.get(3 * size / 4);
        double iqr = q3 - q1;
        double lowerBound = q1 - 1.5 * iqr;
        double upperBound = q3 + 1.5 * iqr;
        ArrayList<Double> filteredList = new ArrayList<>();
        for (double num : list) {
            if (num >= lowerBound && num <= upperBound) {
                filteredList.add(num);
            }
        }
        if (filteredList.isEmpty()) {
            return 50;
        }

        double sum = 0;
        for (double num : filteredList) {
            sum += num;
        }

        return sum / filteredList.size();
    }

    public long averageLong() {
        ArrayList<Long> list = getLongList();
        Collections.sort(list);
        int size = list.size();
        long q1 = list.get(size / 4);
        long q3 = list.get(3 * size / 4);
        double iqr = q3 - q1;
        double lowerBound = q1 - 1.5 * iqr;
        double upperBound = q3 + 1.5 * iqr;
        ArrayList<Long> filteredList = new ArrayList<>();
        for (long num : list) {
            if (num >= lowerBound && num <= upperBound) {
                filteredList.add(num);
            }
        }
        if (filteredList.isEmpty()) {
            return 50;
        }

        long sum = 0;
        for (long num : filteredList) {
            sum += num;
        }

        return sum / filteredList.size();
    }

}
