package com.mogukun.sentry.check.checks.movements.fly;

import com.mogukun.sentry.check.Category;
import com.mogukun.sentry.check.Check;
import com.mogukun.sentry.check.CheckInfo;
import com.mogukun.sentry.check.MovementData;

@CheckInfo(
        name = "Fly (C)",
        path = "movement.fly.c",
        description = "Incorrect Air Vertical Motion",
        category = Category.MOVEMENT
)
public class FlyC extends Check {


    double airTotalDelta = 0;
    double airTotalMotion = 0;
    double buffer = 0;

    int tickSinceUpward = 0;

    @Override
    public void handle(MovementData data)
    {
        if ( isBypass() || !data.moving ) return;

        tickSinceUpward++;
        if ( data.lastY < data.currentY ) {
            tickSinceUpward = 0;
            return;
        }
        if ( tickSinceUpward < 5 ) return;

        if ( data.serverAirTick <= 20 ) {
            airTotalDelta = 0;
            airTotalMotion = 0;
            buffer = 0;
            return;
        }
        if ( data.sinceVehicleTick <= 10 || data.sinceStandingOnBoatTick <= 10 || data.sinceWebTick <= 10 || data.sinceWaterTick <= 10 || data.sinceClimbTick <= 10 ) return;

        airTotalDelta += data.currentDeltaY;
        airTotalMotion += data.lastY - data.currentY;

        double diff = airTotalDelta - airTotalMotion;

        if ( airTotalDelta == 0 || airTotalMotion == 0 ) {
            buffer++;
        }

        if ( diff > 0 ) {
            buffer++;
        } else {
            buffer -= buffer > 0 ? 0.5 : 0;
        }

        if ( buffer > 5 ) {
            flag("diff=" + diff + " atd=" + airTotalMotion + " atm=" + airTotalMotion );
            buffer = 0;
        }
    }
}


