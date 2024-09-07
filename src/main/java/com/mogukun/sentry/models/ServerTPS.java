package com.mogukun.sentry.models;

import com.mogukun.sentry.Sentry;
import org.bukkit.Bukkit;

import java.util.ArrayList;

public class ServerTPS {


    ArrayList<Double> tpsList = new ArrayList<>();

    double tps = 20;
    double exemptTps = 18;

    boolean lagging = false;
    long lastTick = 0;

    public ServerTPS() {
        Bukkit.getScheduler().runTaskTimer(Sentry.instance, new Runnable() {
            @Override
            public void run() {
                watch();
            }
        }, 0L, 0L);
    }

    private void watch() {
        long delta = System.currentTimeMillis() - this.lastTick;


        // credit @derredstoner
        tpsList.add(Math.min(21, ((this.tps * 4d) + ((delta / 50.d) * 20.d)) / 5.d));

        avgTPS();

        this.lagging = this.tps < this.exemptTps;

        lastTick = System.currentTimeMillis();
    }

    public void avgTPS() {

        if ( tpsList.size() < 20 ) return;

        while ( tpsList.size() > 20 ) {
            tpsList.remove(0);
        }

        double sum = 0;
        for ( double tps : tpsList ) sum += tps;
        this.tps = sum / 20;
    }

    public double getTps() {
        return tps;
    }
}
