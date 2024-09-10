package com.mogukun.sentry.check.checks.movements.fly;

import com.mogukun.sentry.check.*;

@CheckInfo(
        name = "Fly (B)",
        path = "movement.fly.b",
        description = "Simple Flight Check",
        category = Category.MOVEMENT
)
public class FlyB extends Check {

    double buffer = 0;


    @Override
    public void handle(MovementData data)
    {
        if ( isBypass() ) return;
        if ( data.sinceWaterTick < 20 ) return;
        if ( data.sinceClimbTick < 10  ) return;
        if ( data.sinceWebTick < 10 ) return;
        if ( data.sinceStandingOnBoatTick <= 5 ) return;
        if ( data.sinceVehicleTick <= 5 ) return;

        if ( data.clientAirTick > 12 && data.currentDeltaY <= data.lastDeltaY  ) {
            if ( buffer++ > 2 ) flag();
        } else {
            buffer -= 1;
        }
    }

}
