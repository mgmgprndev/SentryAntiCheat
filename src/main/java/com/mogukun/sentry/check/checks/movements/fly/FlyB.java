package com.mogukun.sentry.check.checks.movements.fly;

import com.mogukun.sentry.check.*;

@CheckInfo(
        name = "Fly (B)",
        description = "Simple Flight Check",
        category = Category.MOVEMENT
)
public class FlyB extends Check {

    double buffer = 0;

    double lastAccel = 0;

    @Override
    public CheckResult handle(MovementData data)
    {


        if ( data.clientAirTick > 2 && data.currentDeltaY <= data.lastDeltaY  ) {
            if ( buffer++ > 2 ) {
                return new CheckResult();
            }
        } else {
            buffer -= 1;
        }

        return null;

    }

}
