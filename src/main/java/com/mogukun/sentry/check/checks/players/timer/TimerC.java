package com.mogukun.sentry.check.checks.players.timer;

import com.mogukun.sentry.Sentry;
import com.mogukun.sentry.check.Category;
import com.mogukun.sentry.check.Check;
import com.mogukun.sentry.check.CheckInfo;
import com.mogukun.sentry.models.DeltaSample;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ConcurrentLinkedDeque;

@CheckInfo(
        name = "Timer (NEW)",
        description = "Totally Collecting Delay Timer Check",
        category = Category.PLAYER
)
public class TimerC extends Check {

    long lastPacket = 0;
    long startIgnore = 0;

    ConcurrentLinkedDeque<DeltaSample> clientDeltaSample = new ConcurrentLinkedDeque<>();
    ConcurrentLinkedDeque<DeltaSample> serverDeltaSample = new ConcurrentLinkedDeque<>();


    long balance = 0;


    @Override
    public void handle(Packet packet) {
        if ( packet instanceof PacketPlayInFlying )
        {
            long now = System.currentTimeMillis();

            if ( lastPacket == 0 ) {
                lastPacket = now;
                return;
            }

            if ( getPlayerData().teleportTick <= 5 || getPlayerData().respawnTick <= 5 ) {
                lastPacket = now;
                return;
            }

            int tps = (int) Sentry.instance.tps.getTps();

            long clientDelta = now - lastPacket;

            long serverDelta = (1000 / tps);

            clientDeltaSample.add( new DeltaSample(clientDelta) );
            serverDeltaSample.add( new DeltaSample(serverDelta) );

            if ( clientDeltaSample.size() > 100 || serverDeltaSample.size() > 100 ) {


                ArrayList<Long> clientTemp = new ArrayList<>();
                ArrayList<Long> serverTemp = new ArrayList<>();

                for ( DeltaSample d : clientDeltaSample ) {
                    if ( now - d.timeStamp > 8000 ) {
                        clientDeltaSample.remove(d);
                        continue;
                    }
                    clientTemp.add(d.d);
                }
                for ( DeltaSample d : serverDeltaSample ) {
                    if ( now - d.timeStamp > 8000 ) {
                        serverDeltaSample.remove(d);
                        continue;
                    }
                    serverTemp.add(d.d);
                }

                long clientAverage = averageWithoutOutliers(clientTemp);
                long serverAverage = averageWithoutOutliers(serverTemp);
                long diffOfAverage = Math.abs(clientAverage - serverAverage);

                balance += diffOfAverage;
                balance -= balance > 0 && diffOfAverage != 0 ? 1 : 0;

                long d = Math.abs(Math.abs(clientAverage - 50) - Math.abs(serverAverage - 50));

                if ( balance > 50 && d > 1 ) {
                    flag("balance=" + balance + " ca=" + clientAverage + " sa=" + serverAverage + " diff=" + diffOfAverage + " diff2=" + d);
                    balance = 0;
                    clientDeltaSample.clear();
                    serverDeltaSample.clear();
                }
            }

            lastPacket = now;
        }
    }

    @Override
    public void event(Event event) {
        if ( event instanceof PlayerTeleportEvent ) {
            clientDeltaSample.add( new DeltaSample(-50) );
        }
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
