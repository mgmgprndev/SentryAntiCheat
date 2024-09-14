package com.mogukun.sentry.check.checks.combats.autoclicker;

import com.mogukun.sentry.check.Category;
import com.mogukun.sentry.check.Check;
import com.mogukun.sentry.check.CheckInfo;
import com.mogukun.sentry.models.DeltaSample;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedDeque;

@CheckInfo(
        name = "AutoClicker (A)",
        path = "combat.autoclicker.a",
        description = "Simple AutoClicker Check",
        category = Category.COMBAT
)
public class AutoClickerA extends Check {


    ConcurrentLinkedDeque<DeltaSample> samples = new ConcurrentLinkedDeque<>();
    long lastArm = 0;
    int buffer = 0;


    @Override
    public void handle(Packet packet)
    {
        if (packet instanceof PacketPlayInArmAnimation ) {

            long now = System.currentTimeMillis();

            if ( now - getPlayerData().lastPlace  < 5 ) return;
            if ( getPlayerData().isDigging ) return;

            if ( lastArm == 0 ) {
                lastArm = now;
                return;
            }

            // delete old samples
            samples.removeIf(s -> now - s.timeStamp > 5000);

            long delta = now - lastArm;
            samples.add( new DeltaSample(delta) );


            if ( samples.size() < 50 ) {
                lastArm = now;
                return;
            }


            double sum = 0;

            for ( DeltaSample ds : samples ) sum += ds.d;

            int sampleSize = samples.size();

            double mean = sum / sampleSize;
            double squaredSum = 0;

            for ( DeltaSample ds : samples ) squaredSum += Math.pow(ds.d - mean, 2);

            double stdDev = Math.sqrt(squaredSum / sampleSize);
            double threshold = 2 * stdDev;
            ArrayList<Long> filteredSamples = new ArrayList<>();
            for ( DeltaSample ds : samples ) {
                if ( Math.abs(ds.d - mean) <= threshold ) filteredSamples.add(ds.d);
            }

            if (!filteredSamples.isEmpty()) {
                sum = 0;
                for (long value : filteredSamples) {
                    sum += value;
                }
                double newMean = sum / filteredSamples.size();

                squaredSum = 0;
                for (long value : filteredSamples) {
                    squaredSum += Math.pow(value - newMean, 2);
                }
                double newStdDev = Math.sqrt(squaredSum / filteredSamples.size());

                String debug = "deviation=" +  newStdDev + " sample-sizes: f=" + filteredSamples.size() + " o=" + sampleSize;
                if (newStdDev < 20) {
                    if ( buffer++ > config.getIntegerOrDefault("flag_buffer", 1) ) {
                        flag(debug);
                        buffer = 0;
                    }
                } else {
                    //debug("&a" + debug);
                    buffer -= buffer > 0 ? 1 : 0;
                }

            }

            lastArm = now;


        }
    }

}
