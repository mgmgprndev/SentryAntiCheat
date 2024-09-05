package com.mogukun.sentry.models;

public class DeltaSample {
    public long timeStamp;
    public long d;
    public DeltaSample(long d) {
        this.d = d;
        timeStamp = System.currentTimeMillis();
    }
}
