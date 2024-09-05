package com.mogukun.sentry.check.checks.movements.fly;

import com.mogukun.sentry.check.*;

@CheckInfo(
        name = "Fly (A-2)",
        description = "Simple Flight Check",
        category = Category.MOVEMENT
)
public class FlyA2 extends Check {

    double buffer = 0;

    @Override
    public void handle(MovementData data)
    {
        if ( data.sinceWaterTick < 20 ) return;
        if ( data.sinceClimbTick < 10  ) return;

        double accel = data.currentDeltaY - data.lastDeltaY;

        if ( data.clientAirTick > 12 && Math.abs(accel) < 0.01 ) {
            if ( buffer++ > 2 ) flag("accel=" + accel );
        } else {
            buffer -= buffer > 0 ? 0.3 : 0;
        }
    }

}
