package com.mogukun.sentry.check.checks.movements.fly;

import com.mogukun.sentry.check.*;

@CheckInfo(
        name = "Fly (A-1)",
        description = "Simple Flight Check",
        category = Category.MOVEMENT
)
public class FlyA1 extends Check {

    double buffer = 0;
    double y = 0;

    @Override
    public void handle(MovementData data)
    {
        if ( data.sinceWaterTick < 20 ) return;
        if ( data.serverGround ) return;
        if ( data.sinceClimbTick < 10  ) return;
        if ( data.sinceWebTick < 10 ) return;
        if ( data.sinceVehicleTick <= 5 ) return;


        double diff1 = data.lastGroundY - data.serverFallDistance;
        double diff2 = Math.abs(data.currentY - diff1);

        if ( data.clientAirTick < 12 && data.currentY >= data.lastY ) {
            y = data.currentY;
            return;
        }

        double maxDiff = Math.abs(data.lastGroundY - y);
        boolean isViolating = diff2 > maxDiff;

        if ( isViolating ) {
            if ( buffer++ > 5 ) {
                flag(diff2 + " > " + maxDiff );
                buffer = 0;
            }
        } else {
            buffer -= buffer > 0 ? 0.1 : 0;
        }
    }

}
