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
    public CheckResult handle(MovementData data)
    {

        double accel = data.currentDeltaY - data.lastDeltaY;

        if ( data.clientAirTick > 2 && Math.abs(accel) < 0.01 ) {
            if ( buffer++ > 2 ) {
                return new CheckResult("accel=" + accel );
            }
        } else {
            buffer -= buffer > 0 ? 0.3 : 0;
        }

        return null;

    }

}
