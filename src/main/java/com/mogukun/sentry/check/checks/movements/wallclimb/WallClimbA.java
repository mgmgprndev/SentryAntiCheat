package com.mogukun.sentry.check.checks.movements.wallclimb;

import com.mogukun.sentry.check.*;
import com.mogukun.sentry.models.Counter;
import com.mogukun.sentry.models.MovementData;

@CheckInfo(
        name = "WallClimb (A)",
        path = "movement.wallclimb.a",
        description = "Simple WallClimb Check",
        category = Category.MOVEMENT
)
public class WallClimbA extends Check {


    Counter counter = new Counter();

    @Override
    public void handle(MovementData data)
    {
        if ( isBypass() ) return;
        if ( data.sinceWaterTick < 20) return;
        if ( !data.hasHorizontallyColliding ) return;
        if ( data.sinceClimbTick < 15 ) return;

        if ( data.currentY > data.lastY ||
                ( data.currentDeltaY == data.lastDeltaY && data.currentDeltaY > 0.5 )  ) {
            if ( counter.count() > config.getIntegerOrDefault("flag_buffer", 12) ) {
                flag("count="+counter.getCount());
            }

        } else {
            counter.decrease();
        }
    }

}
