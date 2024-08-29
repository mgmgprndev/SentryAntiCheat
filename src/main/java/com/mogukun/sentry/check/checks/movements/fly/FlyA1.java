package com.mogukun.sentry.check.checks.movements.fly;

import com.mogukun.sentry.check.*;

@CheckInfo(
        name = "Fly (A-1)",
        description = "Simple Flight Check",
        category = Category.MOVEMENT
)
public class FlyA1 extends Check {

    double buffer = 0;

    @Override
    public CheckResult handle(MovementData data)
    {


        if ( data.clientAirTick > 2 && data.currentDeltaY > data.lastDeltaY ) {
            if ( buffer++ > 2 ) {
                return new CheckResult("buffer=" + buffer );
            }
        } else {
            buffer -= buffer > 0 ? 0.1 : 0;
        }

        return null;

    }

}
