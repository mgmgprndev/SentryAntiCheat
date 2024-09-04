package com.mogukun.sentry.check.checks.movements.wallclimb;

import com.mogukun.sentry.check.*;
import org.bukkit.potion.PotionEffectType;

@CheckInfo(
        name = "WallClimb (A)",
        description = "Simple WallClimb Check",
        category = Category.MOVEMENT
)
public class WallClimbA extends Check {

    int buffer = 0;

    @Override
    public void handle(MovementData data)
    {
        if (data.sinceWaterTick < 20) return;
        if (data.isInClimb) return;

        if ( data.currentY > data.lastY ||
                ( data.currentDeltaY == data.lastDeltaY && data.currentDeltaY != 0.0 )  ) {
            if ( buffer++ > 6 ) {
                flag();
                buffer = 0;
            }
        } else {
            buffer -= buffer > 0 ? 1 : 0;
        }
    }

}
