package com.mogukun.sentry.check.checks.players.timer;

import com.mogukun.sentry.check.Category;
import com.mogukun.sentry.check.Check;
import com.mogukun.sentry.check.CheckInfo;
import com.mogukun.sentry.check.MovementData;
import com.mogukun.sentry.models.DeltaSample;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedDeque;

@CheckInfo(
        name = "Timer (B)",
        description = "Stupid Timer Check",
        category = Category.PLAYER
)
public class TimerB extends Check {

    ConcurrentLinkedDeque<DeltaSample> samples = new ConcurrentLinkedDeque<>();
    long lastPacket = 0;
    long startIgnore = 0;

    double balance = 0;

    @Override
    public void handle(MovementData data) {

        long now = System.currentTimeMillis();

        if ( now - startIgnore < 0 ) {

            samples.forEach(s -> {
                double diff = Math.abs(now - s.timeStamp);
                if (diff < 500) samples.remove(s);
            });

            return;
        }

        if ( lastPacket == 0 ) {
            lastPacket = now;
            return;
        }

        long delta = now - lastPacket;

        samples.add( new DeltaSample(delta) );
        samples.removeIf(s -> now - s.timeStamp > 5000);

        if ( samples.size() < 50 ) return;

        double possibility = 0;
        double sum = 0;
        for ( DeltaSample s : samples ) {
            sum += s.d;
            if ( s.d == 50 ) { // 50 is legit btw
                possibility -= 1;
                continue;
            }
            double diff = Math.abs(50 - s.d);
            possibility += diff / 10;
        }
        double average = sum / samples.size();

        double x = possibility / samples.size();

        if ( possibility > 50 && x > 1  && (int) average != 50 ) {
            balance += possibility;
            balance -= 50;

            if ( balance > 20 ) {
                flag( "average=" + average +  " possibility=" + possibility + " balance=" + balance + " x=" + x );
                balance = 0;
            }

        }

        //debug( "average=" + average +  " possibility=" + possibility + " amountSample=" + samples.size() );

        lastPacket = now;
    }

    @Override
    public void event(Event event) {
        long now = System.currentTimeMillis();
        if ( event instanceof PlayerTeleportEvent ) {
            startIgnore = now + 50000;
        }
        if ( event instanceof PlayerJoinEvent) {
            startIgnore = now + 50000;
        }
    }

}
