package com.mogukun.sentry.check.checks.movements.fly;

import com.mogukun.sentry.check.Category;
import com.mogukun.sentry.check.Check;
import com.mogukun.sentry.check.CheckInfo;
import com.mogukun.sentry.models.MovementData;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.potion.PotionEffectType;

@CheckInfo(
        name = "Fly (A)",
        path = "movement.fly.a",
        description = "Very Good Upward Fly",
        category = Category.MOVEMENT
)
public class FlyA extends Check {

    double buffer = 0;
    int sincePlaceBlockBelow = 0;

    @Override
    public void handle(MovementData data)
    {
        if ( sincePlaceBlockBelow++ < 5 ) return;
        if ( isBypass() ) return;
        if ( !data.moving ) return;
        if ( data.sinceVehicleTick <= 10 ||
                data.sinceStandingOnBoatTick <= 10 ||
                data.sinceWebTick <= 10 ||
                data.sinceWaterTick <= 10 ||
                data.sinceClimbTick <= 10 ) return;
        if ( data.serverAirTick < 1 ) {
            buffer = 0;
            return;
        }
        int airTick = data.serverAirTick;

        double suspectLevel = 0;

        double maxDeltaY = 0;

        if ( airTick < 6 ) {
            double jumpY = 0.42F;
            if ( getPlayerUtil().getAmplifier(PotionEffectType.JUMP) > 0 ) {
                jumpY += getPlayerUtil().getAmplifier(PotionEffectType.JUMP) * 0.1F;
            }
            if ( airTick != 1 ) {
                jumpY = jumpY - ( (airTick - 1) *  0.077 );
            }
            maxDeltaY = jumpY;
        }

        double diff = Math.abs(floor(data.currentDeltaY) - floor(maxDeltaY));

        if ( ( data.currentDeltaY > maxDeltaY ||  diff > 0.6 ) && data.currentY > data.lastY ) {
            suspectLevel += 0.5;
        }

        if ( data.currentDeltaY == data.lastDeltaY &&
                data.currentDeltaY > 1 ) {
            suspectLevel += 0.5;
        }

        if ( data.currentDeltaY > data.lastDeltaY &&
                airTick < 6 && airTick > 1 ) {
            suspectLevel += 0.5;
        }

        if ( data.currentDeltaY < data.lastDeltaY && airTick > 6 ) {
            suspectLevel += 0.5;
        }

        if ( data.sinceVelocityTaken <= 5 ) {
            suspectLevel -= 0.5;
        }

        if ( suspectLevel > 0 ) {
            buffer += suspectLevel;

            if ( buffer > 5 ) flag();

        }

    }

    private double floor(double number){
        return Math.floor(number * 1000) / 1000;
    }

    @Override
    public void event(Event e) {
        if ( e instanceof BlockPlaceEvent ) {

            BlockPlaceEvent event = (BlockPlaceEvent) e;
            if ( event.getPlayer().getLocation().distance(event.getBlock().getLocation()) < 2 ) {
                sincePlaceBlockBelow = 0;
            }

        }
    }
}



