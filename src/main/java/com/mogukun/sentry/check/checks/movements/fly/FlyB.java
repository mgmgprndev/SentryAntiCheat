package com.mogukun.sentry.check.checks.movements.fly;

import com.mogukun.sentry.check.*;

@CheckInfo(
        name = "Fly (B)",
        description = "Simple Flight Check",
        category = Category.MOVEMENT
)
public class FlyB extends Check {

    double buffer = 0;


    @Override
    public void handle(MovementData data)
    {

        if ( data.sinceWaterTick < 20 ) return;
        if ( data.isInClimb  ) return;

        if ( data.clientAirTick > 12 && data.currentDeltaY <= data.lastDeltaY  ) {
            if ( buffer++ > 2 ) flag();
        } else {
            buffer -= 1;
        }
    }

}
