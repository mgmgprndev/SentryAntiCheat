package com.mogukun.sentry.check.checks.movements.fly;

import com.mogukun.sentry.check.Category;
import com.mogukun.sentry.check.Check;
import com.mogukun.sentry.check.CheckInfo;
import com.mogukun.sentry.check.MovementData;
import com.mogukun.sentry.models.Counter;

@CheckInfo(
        name = "Fly (E)",
        path = "movement.fly.e",
        description = "Unusual DeltaY Detection",
        category = Category.MOVEMENT
)
public class FlyE extends Check {

    Counter counter = new Counter();

    @Override
    public void handle(MovementData data)
    {

        if ( isBypass() || data.serverAirTick <= 6 ) {
            counter.reset();
            return;
        }

        if ( data.sinceVehicleTick <= 10 || data.sinceStandingOnBoatTick <= 10 || data.sinceWebTick <= 10 || data.sinceWaterTick <= 10 || data.sinceClimbTick <= 10 ) return;

        if ( data.currentY < data.lastY && data.currentDeltaY <= data.lastDeltaY ) {
            if ( counter.count() > 5 ) {
                flag();
            }
        }

    }
}


