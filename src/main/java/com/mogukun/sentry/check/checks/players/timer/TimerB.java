package com.mogukun.sentry.check.checks.players.timer;
import com.mogukun.sentry.check.*;
import com.mogukun.sentry.models.*;
import com.mogukun.sentry.models.Counter;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerJoinEvent;
import java.util.*;

@CheckInfo(
        name = "Timer (B)",
        path = "player.timer.b",
        description = "Designed for Stable Timer Check",
        category = Category.PLAYER
)
public class TimerB extends Check {

    long lastPacket = 0;
    long startIgnore = 0;
    double lastPercentage = 0;

    BetterCounter deltaCounter = new BetterCounter(1000 * 60);
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
                long avg = averageWithoutOutliers(deltaCounter.getLongList());

                // somehow this is weird. this return 50% if player is 50% faster.
                double percentage = ((double)avg / 50000000) * 100;
                percentage -= 100; // so get difference.
                percentage = 100 - percentage;

                if( percentage > 105 || percentage < 95 ) {
                    int bufferCount = buffer.count();
                    if ( bufferCount > 10 ) {
                        flag("percentage=" + percentage + " buffer=" + bufferCount + " size=" + deltaCounter.size() );
                        deltaCounter.clear();
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


    public long averageWithoutOutliers(ArrayList<Long> list) {
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
