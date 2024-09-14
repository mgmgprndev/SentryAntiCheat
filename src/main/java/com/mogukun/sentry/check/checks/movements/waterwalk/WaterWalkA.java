package com.mogukun.sentry.check.checks.movements.waterwalk;

import com.mogukun.sentry.check.*;
import com.mogukun.sentry.models.Counter;
import com.mogukun.sentry.models.MovementData;

@CheckInfo(
        name = "WaterWalk (A)",
        path = "movement.waterwalk.a",
        description = "Do not walk on water, jesus!",
        category = Category.MOVEMENT
)
public class WaterWalkA extends Check {


    Counter counter = new Counter();

    @Override
    public void handle(MovementData data)
    {
        if ( isBypass() ) return;
        if (!data.isInLiquid) return;
        if (data.isHittingHead) return;
        if (data.serverGround) return;
        if (data.sinceVehicleTick <= 5 || data.sinceStandingOnBoatTick <= 5) return;

        if ( data.currentDeltaY == data.lastDeltaY ) {
            int count = counter.count();
            if ( count > config.getIntegerOrDefault("flag_buffer", 5) ) {
                flag("count=" + count );
            }
        } else {
            counter.decrease();
        }
    }

}
