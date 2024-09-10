package com.mogukun.sentry.check.checks.players.timer;

import com.mogukun.sentry.Sentry;
import com.mogukun.sentry.check.Category;
import com.mogukun.sentry.check.Check;
import com.mogukun.sentry.check.CheckInfo;
import com.mogukun.sentry.check.MovementData;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

@CheckInfo(
        name = "Timer (A)",
        description = "Timer Check",
        category = Category.PLAYER
)
public class TimerA extends Check {

    double balance = 0;
    long lastPacket = 0;
    long startIgnore = 0;

    @Override
    public void handle(MovementData data) {

        if ( System.currentTimeMillis() - startIgnore < 5000 || data.teleportTick <= 5 || data.respawnTick <= 5 ) return;

        long now = System.currentTimeMillis();

        if ( lastPacket == 0 ) {
            lastPacket = now;
            return;
        }

        long delta = now - lastPacket;



        balance += delta;
        balance -= decrease();

        if ( Math.abs(balance) > 200 ) {
            flag("delta=" + delta + " balance=" + balance);
            balance = 0;
        }

        lastPacket = now;
    }

    @Override
    public void event(Event event) {
        if ( event instanceof PlayerTeleportEvent ) {
            balance -= decrease();
        }
        if ( event instanceof PlayerJoinEvent) {
            startIgnore = System.currentTimeMillis();
        }
    }

    private double decrease(){
        double decrease = 50;
        if ( !Sentry.instance.tps.lagging ) {
            decrease = Math.abs((1000/Sentry.instance.tps.getTps()) / 10) * 10;
        }
        return decrease;
    }
}
