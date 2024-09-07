package com.mogukun.sentry.check.checks.players.timer;

import com.mogukun.sentry.Sentry;
import com.mogukun.sentry.check.Category;
import com.mogukun.sentry.check.Check;
import com.mogukun.sentry.check.CheckInfo;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;

@CheckInfo(
        name = "Timer (B)",
        description = "Advanced Timer Check",
        category = Category.PLAYER
)
public class TimerB extends Check {

    long lastPacket = 0;
    long startIgnore = 0;

    ArrayList<Long> predList = new ArrayList<>();
    ArrayList<Long> realList = new ArrayList<>();


    double buffer = 0;


    @Override
    public void handle(Packet packet) {
        if ( packet instanceof PacketPlayInFlying )
        {
            long now = System.currentTimeMillis();

            if (  now - startIgnore < 5000 || getPlayerData().teleportTick <= 5 || getPlayerData().respawnTick <= 5 ) {
                lastPacket = now;
                return;
            }

            if ( lastPacket == 0 ) {
                lastPacket = now;
                return;
            }

            double tps = ((int)((Sentry.instance.tps.getTps() - 1) * 10D)) / 10D ;

            long delta = now - lastPacket;
            long predicatedDelta = (long) (1000 / tps);

            realList.add( delta );
            predList.add( Math.abs(predicatedDelta) );


            if ( realList.size() > 20 && predList.size() > 20 ) {


                while ( realList.size() > 20 ) {
                    realList.remove(0);
                }

                while ( predList.size() > 20 ) {
                    predList.remove(0);
                }

                long sum1 = 0;
                for ( long difference : realList ) sum1 += difference;
                long realAvg = sum1 / realList.size();

                long sum2 = 0;
                for ( long difference : predList ) sum2 += difference;
                long predAvg = sum2 / predList.size();

                long diffAvg = Math.abs(realAvg - predAvg);

                if ( diffAvg > 1 ) {
                    buffer += diffAvg;
                    buffer -= 1.5;
                    if ( buffer >= 15 ) {
                        flag("buffer=" + buffer + " realAvg=" + realAvg + " predAvg=" + predAvg );
                        buffer = 0;
                    }
                } else {
                    buffer -= buffer > 0 ? 1.5 : 0;
                }

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
