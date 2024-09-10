package com.mogukun.sentry.check.checks.movements.fly;

import com.mogukun.sentry.check.Category;
import com.mogukun.sentry.check.Check;
import com.mogukun.sentry.check.CheckInfo;
import com.mogukun.sentry.check.MovementData;

@CheckInfo(
        name = "Fly (D)",
        description = "Predication Check",
        category = Category.MOVEMENT
)
public class FlyD extends Check {

    double buffer = 0;
    int tickSinceUpward = 0;

    @Override
    public void handle(MovementData data)
    {

        if ( isBypass() ) return;
        if ( !data.moving ) return;
        tickSinceUpward++;
        if ( data.lastY < data.currentY ) {
            tickSinceUpward = 0;
            return;
        }
        if ( tickSinceUpward < 5 ) return;
        if ( data.currentDeltaY > 1 ) return;
        if ( data.serverAirTick <= 16 ) {
            buffer = 0;
            return;
        }
        if ( data.sinceVehicleTick <= 10 || data.sinceStandingOnBoatTick <= 10 || data.sinceWebTick <= 10 || data.sinceWaterTick <= 10 || data.sinceClimbTick <= 10 ) return;

        // Credit: CheatGuard @DerRedstoner
        double predicated = ( data.lastDeltaY - 0.08 ) * 0.9800000190734863;
        double difference = Math.abs(predicated - data.currentDeltaY);

        if ( difference > 0.001 ) {
            buffer += difference * 10;
            if ( buffer > 10 ) {
                flag("pred=" + predicated + " real=" + data.currentDeltaY + " diff=" + difference );
                buffer -= 1;
            }
        } else {
            buffer -= buffer > 0 ? 0.5 : 0;
        }
    }
}


