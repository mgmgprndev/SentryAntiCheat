package com.mogukun.sentry.check.checks.combats.autoclicker;

import com.mogukun.sentry.check.Category;
import com.mogukun.sentry.check.Check;
import com.mogukun.sentry.check.CheckInfo;
import com.mogukun.sentry.models.Configuration;
import com.mogukun.sentry.models.Counter;
import com.mogukun.sentry.models.DeltaSample;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInArmAnimation;
import net.minecraft.server.v1_8_R3.PacketPlayInBlockDig;
import net.minecraft.server.v1_8_R3.PacketPlayInBlockPlace;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedDeque;

@CheckInfo(
        name = "AutoClicker (B)",
        path = "combat.autoclicker.b",
        description = "Simple AutoClicker Check",
        category = Category.COMBAT
)
public class AutoClickerB extends Check {

    long lastArm = 0;

    ConcurrentLinkedDeque<DeltaSample> samples = new ConcurrentLinkedDeque<>();


    @Override
    public void handle(Packet packet)
    {
        if (packet instanceof PacketPlayInArmAnimation ) {

            long now = System.nanoTime();

            if ( now - getPlayerData().lastPlace  < 5 ) return;
            if ( getPlayerData().isDigging ) return;

            if ( lastArm == 0 ) {
                lastArm = now;
                return;
            }

            samples.removeIf(s -> now - s.timeStamp > 1000000000 );

            long delta = now - lastArm;
            samples.add( new DeltaSample(delta) );

            if ( samples.size() > 10 ) {

                for ( DeltaSample s : samples );

            }

            lastArm = now;

        }
    }

}
