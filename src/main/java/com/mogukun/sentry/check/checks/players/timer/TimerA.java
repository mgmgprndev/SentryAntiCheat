package com.mogukun.sentry.check.checks.players.timer;
import com.mogukun.sentry.check.*;
import com.mogukun.sentry.models.*;
import com.mogukun.sentry.models.Counter;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerJoinEvent;

@CheckInfo(
        name = "Timer (A)",
        path = "player.timer.a",
        description = "Stable Timer Check, Flawless Design",
        category = Category.PLAYER
)
public class TimerA extends Check {

    long lastPacket = 0;
    long startIgnore = 0;
    double lastPercentage = 0;

    BetterCounter deltaCounter = new BetterCounter(1000 * 60);
    BetterCounter percentageCounter = new BetterCounter(1000 * 60);
    Counter buffer = new Counter();

    @Override
    public void handle(Packet packet) {
        if ( packet instanceof PacketPlayInFlying )
        {
            long now = System.nanoTime();

            if ( lastPacket == 0 ||
                    getPlayerData().teleportTick <= 5 ||
                    getPlayerData().respawnTick <= 5 ||
                    System.currentTimeMillis() - startIgnore < 10000 ) {

                lastPacket = now;
                return;
            }
            deltaCounter.count( now - lastPacket );

            if ( deltaCounter.size() > 60 ) {
                long avg = deltaCounter.averageLong(); //averageWithoutOutliers(deltaCounter.getLongList());

                // somehow this is weird. this return 50% if player is 50% faster.
                double tickDelay = 50000000; //1000000000 / Sentry.instance.tps.tps; (IDK)
                double percentage = ((double)avg / tickDelay) * 100;
                percentage -= 100; // so get difference.
                percentage = 100 - percentage;

                percentageCounter.count(percentage);
                percentage = percentageCounter.averageDouble();

                if ( percentageCounter.size() > 60 ) {
                    if( percentage > 102 || percentage < 98 ) {
                        int bufferCount = buffer.count();
                        if ( bufferCount > 10 ) {
                            flag("percentage=" + percentage + " buffer=" + bufferCount + " size=" + deltaCounter.size() );
                            deltaCounter.clear();
                            percentageCounter.clear();
                        }
                    }
                }


                lastPercentage = percentage;
            }


            lastPacket = now;
        }
    }

    @Override
    public void event(Event event) {
        if ( event instanceof PlayerJoinEvent) {
            startIgnore = System.currentTimeMillis();
        }
    }

}
