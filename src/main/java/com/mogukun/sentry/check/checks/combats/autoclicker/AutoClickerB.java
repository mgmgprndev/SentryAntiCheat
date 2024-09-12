package com.mogukun.sentry.check.checks.combats.autoclicker;

import com.mogukun.sentry.check.Category;
import com.mogukun.sentry.check.Check;
import com.mogukun.sentry.check.CheckInfo;
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


    boolean isBreaking = false;
    long lastArm = 0;
    long lastPlace = 0;

    ConcurrentLinkedDeque<DeltaSample> samples = new ConcurrentLinkedDeque<>();
    int buffer = 0;


    @Override
    public void handle(Packet packet)
    {
        if (packet instanceof PacketPlayInArmAnimation ) {

            long now = System.currentTimeMillis();

            if ( now - lastPlace  < 5 ) return;
            if ( isBreaking ) return;

            if ( lastArm == 0 ) {
                lastArm = now;
                return;
            }

            samples.removeIf(s -> now - s.timeStamp > 1000);

            long delta = now - lastArm;
            samples.add( new DeltaSample(delta) );

            long average = 0;
            long oldestTime = Long.MAX_VALUE;
            for ( DeltaSample d : samples ) {
                average+=d.d;
                if ( oldestTime > d.timeStamp ){
                    oldestTime = d.timeStamp;
                }
            }
            average /= samples.size();

            long predicatedAvg = (now - oldestTime) / samples.size();

            long difference = predicatedAvg - average;

            //debug("difference=" + difference + " average=" + average  + " predicated=" + predicatedAvg + " current=" + delta);


            lastArm = now;

        } else if ( packet instanceof PacketPlayInBlockDig ) {
            PacketPlayInBlockDig p = (PacketPlayInBlockDig) packet;
            if(p.c() == PacketPlayInBlockDig.EnumPlayerDigType.START_DESTROY_BLOCK) isBreaking = true;
            if(p.c() == PacketPlayInBlockDig.EnumPlayerDigType.STOP_DESTROY_BLOCK) isBreaking = false;
            if(p.c() == PacketPlayInBlockDig.EnumPlayerDigType.ABORT_DESTROY_BLOCK) isBreaking = false;
        } else if ( packet instanceof PacketPlayInBlockPlace ) {
            lastPlace = System.currentTimeMillis();
        }
    }

    @Override
    public void event(Event event){
        // this is because only this called if this event by the player. no need for check that.
        if ( event instanceof EntityDamageByEntityEvent ) isBreaking = false;
    }

}
