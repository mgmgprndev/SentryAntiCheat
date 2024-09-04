package com.mogukun.sentry.check.checks.movements.waterwalk;

import com.mogukun.sentry.check.*;
import org.bukkit.potion.PotionEffectType;

@CheckInfo(
        name = "WaterWalk (A)",
        description = "Do not walk on water, jesus!",
        category = Category.MOVEMENT
)
public class WaterWalkA extends Check {


    int buffer = 0;

    @Override
    public void handle(MovementData data)
    {
        if (!data.isInLiquid) return;
        if (data.isHittingHead) return;
        if (data.serverGround) return;

        if ( data.currentDeltaY == data.lastDeltaY ) {
            if ( buffer++ > 2 ) {
                flag();
                buffer = 0;
            }
        } else {
            buffer -= buffer > 0 ? 1 : 0;
        }
    }

}
