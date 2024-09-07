package com.mogukun.sentry.models;

import java.util.ArrayList;
import java.util.Iterator;

public class Counter {

    ArrayList<Long> counts = new ArrayList<>();

    public Counter(){

    }

    public int count(){
        counts.add(System.currentTimeMillis());
        return getCount();
    }

    public void decrease() {
        if (!counts.isEmpty()) {
            counts.remove(0);
        }
    }

    public int getCount() {
        Iterator<Long> iterator = counts.iterator();
        while (iterator.hasNext()) {
            Long i = iterator.next();
            if (System.currentTimeMillis() - i >= 1000) {
                iterator.remove();
            }
        }
        return counts.size();
    }

    public void reset() {
        counts = new ArrayList<>();
    }

}
