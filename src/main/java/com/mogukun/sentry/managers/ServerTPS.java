package com.mogukun.sentry.managers;

import com.mogukun.sentry.Sentry;
import org.bukkit.Bukkit;

import java.util.ArrayList;

public class ServerTPS {


    ArrayList<Double> tpsList = new ArrayList<>();

    public double tps = 20;
    public double exemptTps = 18;

    public boolean lagging = false;
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
        long now = System.currentTimeMillis();
        long delta = now - this.lastTick;
        lastTick = now;

        new Thread(() -> {
            // credit @derredstoner
            tpsList.add(Math.min(21, ((Sentry.instance.tps.tps * 4d) + ((delta / 50.d) * 20.d)) / 5.d));

            if ( tpsList.size() < 20 ) return;

            while ( tpsList.size() > 20 ) {
                tpsList.remove(0);
            }

            double sum = 0;
            for ( double tps : tpsList ) sum += tps;
            Sentry.instance.tps.tps = sum / 20;

            Sentry.instance.tps.lagging = Sentry.instance.tps.tps < Sentry.instance.tps.exemptTps;
        }).start();

    }

    public double getTps() {
        return tps;
    }
}
